package com.example.medbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.HashMap;


public class ScanFragment extends Fragment {

    private static final String TAG = "PestIdentifyFragment";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private Button buttonTakePicture, buttonLaunchGallery, buttonPredict;
    private ImageView imageView;
    private ProgressBar progressBar; // Add a ProgressBar reference

    HashMap<String, String> medData = new HashMap<>();

    private int imageSize = 224;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);


        buttonTakePicture = view.findViewById(R.id.button);
        buttonLaunchGallery = view.findViewById(R.id.button2);
        buttonPredict = view.findViewById(R.id.buttonPredict);
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar); // Initialize ProgressBar

        buttonTakePicture.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        buttonLaunchGallery.setOnClickListener(v -> openGallery());

        buttonPredict.setOnClickListener(v -> {
            if (imageView.getDrawable() != null) {
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                classifyImage(image);
            } else {
                showToast("Please select an image first.");
            }
        });

        medData.put("Acemiz PLUS",
                "Product Name: Acemiz® PLUS\n" +
                        "Composition:\n" +
                        "• Aceclofenac IP: 100 mg\n" +
                        "• Paracetamol IP: 325 mg\n" +
                        "Dosage: As directed by the physician\n" +
                        "Storage: Store protected from light & moisture, at a temperature not exceeding 30°C. Keep out of reach of children.\n" +
                        "Manufacturer: Prakruti Life Science Private Limited\n" +
                        "• Address: No. 256/2A & 2B, Neelavar Village, Kunjal, Brahmavar, Udupi Taluk, Udupi - 575213, INDIA\n" +
                        "Manufactured Under License: KTK/25/632/2014\n" +
                        "Batch Number: ACZ-24028\n" +
                        "MRP: ₹119.75\n" +
                        "Manufacturing Date: 03/2024\n" +
                        "Expiry Date: 02/2026\n" +
                        "Other Information:\n" +
                        "• Contains Sunset Yellow and Titanium Dioxide IP\n" +
                        "• Not for veterinary use\n" +
                        "• Manufactured by: Lupin Ltd., Bldg No. A3, Shiv Shanti Industrial & Logistics Park, Ghar No. 598, Ghodbunder Road, Bhiwandi Dist. Thane (421 306), Maharashtra, India\n" +
                        "• Marketing Authorisation Holder: Lupin Ltd.\n" +
                        "• To report product complaints or adverse drug reactions, dial Toll-Free no. 1800-209-2505 or email at [email address removed]"
        );

        medData.put("AzithroSafe-500",
                "Product Name: AzithroSafe-500\n" +
                        "Composition:\n" +
                        "• Azithromycin IP: 500 mg (as Dihydrate equivalent to Anhydrous Azithromycin)\n" +
                        "• Excipients: Not specified\n" +
                        "• Color: Titanium Dioxide IP\n" +
                        "Dosage: As directed by the physician\n" +
                        "Storage: Store in a cool, dry, and dark place. Keep out of reach of children.\n" +
                        "Manufacturer: Anon Pharmaceutical\n" +
                        "• Address: A-1, R.I.A. Harsuliya - 303005\n" +
                        "Manufactured in India by: Anon Pharmaceutical\n" +
                        "• Manufacturing License No.: RAJ-2641/RAJ-2642\n" +
                        "Marketed by: Dr. Morepen\n" +
                        "• Address: 669 Antriksh Bhawan, 12 KG Marg, New Delhi - 110 001\n" +
                        "• Customer Care No.: +91-8327006592\n" +
                        "• Email: [email address removed]\n" +
                        "Batch Number: SAI-23310\n" +
                        "MRP: ₹79.16\n" +
                        "MFG. Date: 12/2023\n" +
                        "EXP. Date: 11/2025\n" +
                        "For: 3 Tablets\n" +
                        "Schedule: Prescription drug\n" +
                        "Caution: This medication should only be used under the direction of a qualified healthcare professional."
        );

        medData.put("Crocin 650",
                "Product Name: Crocin 650\n" +
                        "Composition: Paracetamol I.P. 650 mg\n" +
                        "Dosage: 1 tablet at every 4-6 hours. Maximum dose in 24 hours: 6 tablets. Do not exceed stated dose. Use the smallest dose for shortest period necessary. Do not take more than 3 days without medical advice. Do not use in children under 12 years of age.\n" +
                        "Patient Advice:\n" +
                        "• Do not use with other paracetamol-containing products.\n" +
                        "• Do not use if allergic to paracetamol, any other ingredient in the product, aspirin, non-steroidal anti-inflammatory drugs.\n" +
                        "• Seek medical advice if: Symptoms persist, patient is suffering from liver or kidney problems, taking blood thinning medication/overdose, allergy, unexplained bleeding/severe infection, underweight, drink alcohol, pregnant.\n" +
                        "Storage: Keep out of sight and reach of children. Store at ambient room temperature, protected from light and moisture. Product is protected in a sealed blister. Do not use if the blister or foil is broken.\n" +
                        "Warnings: Taking more than the maximum recommended daily dose of paracetamol may cause serious liver damage or allergic reactions (e.g., swelling of the face, mouth, and throat, difficulty in breathing, itching, or rash).\n" +
                        "Manufactured By: Encore Healthcare Pvt. Ltd., Plot No-D5, MIDC, Industrial Area, Paithan, Aurangabad-431 148, Maharashtra, India\n" +
                        "Marketed By: GlaxoSmithKline Asia Pvt. Ltd., Patiala Road, Nabha-147 201, Punjab, India\n" +
                        "Optizorb Technology: Starts releasing its medicine in 5 minutes.\n" +
                        "Customer Care: 000 800 442 0168 [email protected]\n" +
                        "Trademarks: Owned by or licensed to the GSK group of companies.\n" +
                        "Batch Number (BC-CC-15-12-18-ENC): [Replace with the actual batch number from the image]\n" +
                        "Other Information:\n" +
                        "• Contains Paracetamol I.P. 650 mg\n" +
                        "• [Other information from the image, if applicable]"
        );

        medData.put("Gelusil Chewable Tablets",
                "Product Name: Gelusil Chewable Tablets (Original)\n" +
                        "Composition:\n" +
                        "• Activated Dimethicone I.P.: 50 mg\n" +
                        "• Magnesium Hydroxide I.P.: 250 mg\n" +
                        "• Dried Aluminum Hydroxide I.P.: 250 mg\n" +
                        "• Magnesium Aluminum Silicate Hydrate: 50 mg\n" +
                        "• Colors: Erythrosine and Ponceau 4R\n" +
                        "Dosage: 1 to 2 tablets to be chewed 1/2-1 hour after each meal or whenever symptoms are pronounced.\n" +
                        "Warning: Do not take more than 12 tablets in a 24-hour period. This product should not be employed for more than 2 weeks, except under the strict supervision of a physician.\n" +
                        "Storage: Keep out of reach of children. Store in a dry place away from sunlight.\n" +
                        "Manufactured By: Pfizer Limited\n" +
                        "• Address: No. 45, Mangalam Main Road, Mangalam Village, Villianur Commune, Puducherry-605 110.\n" +
                        "Trademark Proprietor: Warner-Lambert Company LLC, USA\n" +
                        "Licensed User: Pfizer Limited, India\n" +
                        "Customer Care: 1800-22-4446\n" +
                        "Website: www.gelusil.in\n" +
                        "Batch Number: [Batch number from the image]\n" +
                        "Other Information:\n" +
                        "• Contains 15 tablets\n" +
                        "• Quick & lasting relief from acidity, heartburn, and gas"
        );

        medData.put("MultiRich",
                "Product Name: MultiRich\n" +
                        "Composition:\n" +
                        "• Each soft gelatin capsule contains:\n" +
                        "  o Di Basic Calcium Phosphate: 90 mg (Eq. to elemental Calcium: 50 mg)\n" +
                        "  o Magnesium Oxide: 51 mg (Eq. to elemental Magnesium: 31 mg)\n" +
                        "  o Vitamin C (Ascorbic Acid): 25 mg\n" +
                        "  o Niacinamide: 18 mg\n" +
                        "  o Zinc Sulphate Monohydrate: 12 mg (Eq. to elemental Zinc: 7.8 mg)\n" +
                        "  o L-Lysine: 10 mg\n" +
                        "  o Vitamin E Acetate: 7.5 mg\n" +
                        "  o Calcium D Pantothenate: 2.5 mg\n" +
                        "  o Manganese Sulphate: 2 mg (Eq. to elemental Manganese: 1 mg)\n" +
                        "  o Vitamin B2 (Riboflavin): 1.6 mg\n" +
                        "  o Vitamin B1 (Thiamine Mononitrate): 1.4 mg\n" +
                        "  o Copper Sulphate Pentahydrate: 1 mg (Eq. to elemental Copper: 0.5882 mg)\n" +
                        "  o Vitamin B6 (Pyridoxine HCI): 0.5 mg\n" +
                        "  o Vitamin A (as Palmitate): 480 mcg\n" +
                        "  o Folic Acid: 117.64 mcg\n" +
                        "  o Vitamin D3: 2.5 mcg\n" +
                        "  o Vitamin B12 (Cyanocobalamin): 0.5 mcg\n" +
                        "Other Ingredients: Gelatin (Animal Origin), Sorbitol (Stabilizer), Refined Soybean Oil (Vehicle), Glycerin (Humectant), Soya Lecithin (Emulsifier), BHA (Antioxidant), Hydrogenated Vegetable Oil (Lubricant), Beeswax (Glazing Agent), Sodium Methylparaben Preservative, Sodium Propyl Paraben Preservative, Light Liquid Paraffin.\n" +
                        "Nutritional Information (Per Serving):\n" +
                        "• Quantity Per Capsule: 1\n" +
                        "• Nutrients:\n" +
                        "  o Protein: 0.057 gm\n" +
                        "  o Fat: 0.36 gm\n" +
                        "  o Carbohydrate: 0.011 gm\n" +
                        "  o Energy: 3.51 k.cal\n" +
                        "Health Supplement\n" +
                        "Not For Medicinal Use\n" +
                        "Manufactured in India by: Marine Lifesciences, Plot No. 30, Ph-III (Ext.), HPSIDC, Baddi, Distt. Solan-173 205 (HP)\n" +
                        "FSSAI Lic. No.: 10914011000657\n" +
                        "Marketed By: Lupin Ltd.\n" +
                        "• Address: 159, C.S.T. Road, Kalina, Santacruz (E), Mumbai-400 098, India\n" +
                        "• Toll-Free No.: 1800-200-2505\n" +
                        "• Email: [email protected]\n" +
                        "Batch Number: [Batch number from the image]\n" +
                        "MFG. Date: [MFG. Date from the image]\n" +
                        "EXP. Date: [EXP. Date from the image]\n" +
                        "MRP: [MRP from the image] (Inclusive of All Taxes)\n" +
                        "For: 10 Capsules\n" +
                        "Other Information:\n" +
                        "Product Information:\n" +
                        "• Contains: Permitted natural color, permitted class II preservatives\n" +
                        "• RDA: Calculated based on ICMR Guidelines for moderate working men (RDA not established for certain nutrients)\n" +
                        "• Storage: Store in a cool, dry, and dark place below 30°C. Protect from light, heat, and moisture. Keep out of reach of children.\n" +
                        "• Usage: One capsule per day after a meal or as directed by the Nutritionist/Dietician. Do not exceed the recommended daily usage.\n" +
                        "• Best Before: 24 months from manufacture\n" +
                        "Manufacturer: Marine Lifesciences, Plot No. 30, Ph-III (Ext.), HPSIDC, Baddi, Distt. Solan-173 205 (HP)\n" +
                        "• FSSAI Lic. No.: 10914011000657\n" +
                        "Marketed By: Lupin Ltd., 159, C.S.T. Road, Kalina, Santacruz (E), Mumbai - 400 098, INDIA\n" +
                        "Additional Information:\n" +
                        "• Capsule should be swallowed whole, not chewed or crushed.\n" +
                        "• Appropriate overage of vitamins is added to compensate for loss on storage.\n" +
                        "• This product is not intended to diagnose, treat, cure, or prevent any disease.\n" +
                        "• This product should not be used as a substitute for a varied diet.\n" +
                        "• Consult your Nutritionist/Dietician for pregnant, lactating, or taking other medications."
        );

        medData.put("ALMOX-500",
                "Product Name: ALMOX-500\n" +
                        "हिंदी में नाम: एल्मॉक्स - 500\n" +
                        "Composition: Each hard gelatin capsule contains:\n" +
                        "• Amoxicillin Trihydrate IP: Equivalent to Amoxicillin 500 mg\n" +
                        "• Excipients: q.s.\n" +
                        "• Colors: Carmoisine, Titanium Dioxide IP & Tartrazine used in empty hard gelatin capsule shell.\n" +
                        "Storage: Store protected from moisture at a temperature not exceeding 30°C.\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Caution: Keep medicine out of reach of children.\n" +
                        "Manufacturing Information:\n" +
                        "• Mfg. Lic. No.: [Manufacturing License Number from the image]\n" +
                        "• Made in India by: ALKEM LABORATORIES LTD., At: Village-Thana, Tehsil-Baddi, Dist.-Solan, Himachal Pradesh-173 205.\n" +
                        "• H.O.: ALKEM HOUSE, Senapati Bapat Marg, Lower Parel, Mumbai - 400 013.\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                        "Brand: ALMOX-500"
        );

        medData.put("Coldimac",
                "Product Name: Coldimac\n" +
                        "Composition:\n" +
                        "• Chlorpheniramine Maleate IP: 4 mg\n" +
                        "• Phenylephrine Hydrochloride IP: 5 mg\n" +
                        "• Excipients\n" +
                        "• Approved Colour (Erythrosine) & Preservative (Bronopol (P) used in empty capsule shell)\n" +
                        "Dosage: As directed by the physician.\n" +
                        "Storage: Store in a cool, dry, and dark place. Keep out of reach of children.\n" +
                        "Manufacturing Information:\n" +
                        "• Mfg. Lic. No.: 23/UA(2014)\n" +
                        "• Manufactured By: Proshma Care Pvt. Ltd., Khasra No. 68, 69, 71, Village: Sikandarpur, Bhaiswal, Near Bhagwanpur Roorkee, Dist. Haridwar 247 661 (UK) India\n" +
                        "Marketed By: MACRO Pharmaceuticals (An ISO 9001:2015 Certified Co.), CHENNAI-600112 (INDIA)\n" +
                        "Other Information:\n" +
                        "• Administration of Coldimac Capsules may cause drowsiness in some individuals.\n" +
                        "• Therefore, driving of vehicles and running of machinery should be avoided after taking the medicine.\n" +
                        "• Batch Number: MCCX0363\n" +
                        "• MRP: ₹45.00 (Inclusive of All Taxes)\n" +
                        "• Mfg. Date: 03/2024\n" +
                        "• Expiry Date: 02/2026\n" +
                        "• For: 15 Capsules"
        );

        medData.put("Diclown Plus",
                "Product Name: Diclown Plus\n" +
                        "हिंदी में नाम: डिक्लोविन प्लस\n" +
                        "Composition: Each uncoated tablet contains:\n" +
                        "• Diclofenac Sodium IP (Gastro-resistant coated): 50 mg\n" +
                        "• Paracetamol IP: 325 mg\n" +
                        "Dosage: As prescribed by the Physician. Do not exceed recommended dose.\n" +
                        "Storage: Store protected from light and moisture at a temperature below 30°C.\n" +
                        "Warning: Keep out of reach of children.\n" +
                        "Manufactured By: Wings Biotech LLP, 43 & 44, HPSIDC Industrial Area, Baddi-173 205 (HP)\n" +
                        "Marketed By: Wings Pharmaceuticals Pvt. Ltd., D-6, Udyog Nagar Industrial Area, New Delhi-110041\n" +
                        "Customer Care: 1800-120-5261 [email protected]\n" +
                        "Batch Number: DP-5515\n" +
                        "MRP: Rs. 26.60 (Inclusive of All Taxes)\n" +
                        "MFG. Date: Apr. 2024\n" +
                        "EXPIRY DATE: Mar. 2027\n" +
                        "For: 10 Tablets\n" +
                        "Other Information:\n" +
                        "• Contains Diclofenac Sodium and Paracetamol Tablets IP\n" +
                        "• Schedule H Prescription Drug Caution\n" +
                        "• Not to be sold by retail without the prescription of a Registered Medical Practitioner\n" +
                        "• Registered Trademark. Copyright reserved."
        );

        medData.put("Glycinorm-M 40",
                "Product Name: Glycinorm-M 40\n" +
                        "हिंदी में नाम: ग्लायसिनोर्म एम 40\n" +
                        "Composition: Each uncoated tablet contains:\n" +
                        "• Gliclazide IP: 40 mg\n" +
                        "• Metformin Hydrochloride IP: 500 mg\n" +
                        "• Excipients: q.s.\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Store in a dry place, protected from light & moisture. Keep out of reach of children.\n" +
                        "Manufacturing Information:\n" +
                        "• M. L. No.: M/575/2011\n" +
                        "• Made in India by: Ipca Laboratories Ltd., 393/994 Melil Jorethang Road, Gom Block, Bharikhola, South District, Sikkim 737 121.\n" +
                        "• Regd. Off.: 48, Kand Ind. Estate, Mumbai 400 067\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                        "Batch Number: B.No.TJ054004AS\n" +
                        "MRP: ₹129.70 (Inclusive of All Taxes)\n" +
                        "MFG. Date: Apr. 2024\n" +
                        "EXP. Date: Mar. 2026\n" +
                        "For: 15 Tablets"
        );

        medData.put("Glycirol-M 500",
                "Product Name: Glycirol-M 500\n" +
                        "Composition: Each uncoated sustained release tablet contains:\n" +
                        "• Metformin Hydrochloride I.P.: 500 mg\n" +
                        "• Excipients: q.s.\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Store in a cool, dry, and dark place.\n" +
                        "Tablet Instructions: Tablet should be swallowed whole and not to be chewed or crushed. Keep the medicine out of reach of children.\n" +
                        "Manufacturing Information:\n" +
                        "• Mfg. Lic. No.: TN00005018\n" +
                        "• Manufactured by: Middle Mist Pharmaceuticals Pvt. Ltd., An ISO 9001:2015 Certified Company, #1/230, Leelavathy Nagar, Kundrathur Main Road, Chikkarayapuram, Chennai-69.\n" +
                        "Marketed By: Deekay Lifescience, Old No.: 25/3, New No.: 51/3, C.R. Garden Street, Chennai-12.\n" +
                        "Batch Number: B.No.MMPT4946\n" +
                        "MRP: Rs. 23.80 (Inclusive of All Taxes)\n" +
                        "MFG. Date: Sep. 2023\n" +
                        "EXP. Date: Aug. 2025\n" +
                        "For: 10 Tablets\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. CAUTION: It is dangerous to take this preparation except under medical supervision."
        );

        medData.put("Glycomet GP 1",
                "Product Name: Glycomet GP 1\n" +
                        "Composition: Each uncoated tablet contains:\n" +
                        "• Metformin Hydrochloride IP (as prolonged-release): 500 mg\n" +
                        "• Glimepiride IP: 1 mg\n" +
                        "Color: Red Oxide of Iron\n" +
                        "Excipients: q.s.\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Protect from moisture and excessive heat.\n" +
                        "Manufacturing Information:\n" +
                        "• Manufactured by: USV Private Limited, Plot No. 538 to 540, 597 to 618, 619 to 625, GIDC Industrial Area, Savli, Taluka Savli, Manjusar, Vadodara-391 775.\n" +
                        "• Mfg. Lic. No.: G/25/2569\n" +
                        "Marketed By: USV Private Limited\n" +
                        "Customer Care: For any product-related comments, please write to [email protected]\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. CAUTION: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                        "Batch Number: 60001234\n" +
                        "MFG. Date: 05/2024\n" +
                        "EXP. Date: 04/2026\n" +
                        "MRP: Rs. 122.00 (Inclusive of All Taxes)\n" +
                        "For: 15 Tablets"
        );

        medData.put("MEFTAL-SPAS",
                "Product Name: MEFTAL-SPAS\n" +
                        "हिंदी में नाम: मेफ्टाल-स्पास\n" +
                        "Composition: Each Uncoated Tablet Contains:\n" +
                        "• Mefenamic Acid IP: 250 mg\n" +
                        "• Dicyclomine Hydrochloride IP: 10 mg\n" +
                        "• Excipients: q.s.\n" +
                        "• Color: Tartrazine\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. CAUTION: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                        "Dosage: As prescribed by the Physician.\n" +
                        "Storage: Keep out of reach of children. Store protected from light and moisture at a temperature not exceeding 30°C.\n" +
                        "Manufacturing Information:\n" +
                        "• Mfg. Lic. No.: BD/25\n" +
                        "• Made in India By: BLUE CROSS LABORATORIES PVT LTD., A-12, M.I.D.C., NASHIK-422 010.\n" +
                        "• Regd. Off.: Peninsula Chambers GK, Marg, Mumbai - 400 013.\n" +
                        "Batch Number: MST2423\n" +
                        "MFG. Date: 04/2024\n" +
                        "EXPIRY DATE: 03/2027\n" +
                        "MRP: Rs. 52.00 (Inclusive of All Taxes)\n" +
                        "For: 10 Tablets"
        );

        medData.put("Nicip Plus",
                "Product Name: Nicip Plus\n" +
                        "Composition: Each uncoated tablet contains:\n" +
                        "• Nimesulide BP: 100 mg\n" +
                        "• Paracetamol IP: 325 mg\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Store in a cool, dry place. Protect from light.\n" +
                        "Warning: Not for use by children below the age of 12 years.\n" +
                        "Caution: Taking more than the daily dose of Paracetamol may cause serious liver damage or allergic reactions (e.g., swelling of the face, mouth, and throat, difficulty in breathing, itching, or rash).\n" +
                        "Additional Information:\n" +
                        "• Use of Nimesulide should ordinarily be restricted to 10 days. If longer clinical use is warranted, liver function tests should be assessed periodically.\n" +
                        "• Manufactured by: Cipla Ltd.\n" +
                        "• M.L. No.: 61/UA/LL/2018\n" +
                        "• Batch Number: [Batch number from the image]\n" +
                        "• MFG. Date: [MFG. Date from the image]\n" +
                        "• EXP. Date: [EXP. Date from the image]\n" +
                        "• MRP: [MRP from the image] (Inclusive of All Taxes)\n" +
                        "• For: 15 Tablets"
        );

        medData.put("Nicotex",
                "Product Name: Nicotex\n" +
                        "Nicotine Polacrilex Gum USP 4 mg\n" +
                        "Sugar-Free\n" +
                        "Ultra Mint Flavor\n" +
                        "Composition: Each gum contains Nicotine Polacrilex USP equivalent to Nicotine 4 mg.\n" +
                        "• Color: Titanium Dioxide IP\n" +
                        "Dosage: As directed by the physician.\n" +
                        "Warning: To be sold by retail on the prescription of a Registered Medical Practitioner only.\n" +
                        "Storage: Store in a cool, dry place. Protect from direct heat and sunlight. Keep out of reach of children.\n" +
                        "Manufacturing Information:\n" +
                        "• Mfd. by CIPLA HEALTH LTD., M-32-38, MIDC, Hingna Road, Nagpur 440 016, India.\n" +
                        "• M.L. No.: MH/101578A\n" +
                        "Customer Care: 1800 120 143143 [email protected]\n" +
                        "Website: www.nicotex.in\n" +
                        "Other Information:\n" +
                        "• Cipla is a registered trademark of Cipla Limited.\n" +
                        "• Used under a non-exclusive license from Cipla Limited.\n" +
                        "• Nicotex is the registered trademark of Cipla Limited.\n" +
                        "• How to chew:\n" +
                        "  o Step 1: Chew it slowly till you get the taste of nicotine.\n" +
                        "  o Step 2: Place & Rest between your cheek & gums.\n" +
                        "  o Step 3: When the taste fades, repeat Step 1."
        );

        medData.put("OFLOTAS-OZ",
                "Product Name: OFLOTAS-OZ\n" +
                        "हिंदी में नाम: ओफ्लोटास-ओज़\n" +
                        "Composition: Each film coated tablet contains:\n" +
                        "• Ofloxacin I.P.: 200 mg\n" +
                        "• Ornidazole I.P.: 500 mg\n" +
                        "• Excipients: q.s.\n" +
                        "• Colors: Sunset Yellow FCF, Tartrazine & Titanium Dioxide I.P.\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Store protected from light and moisture, at a temperature not exceeding 30°C. Keep medicines out of reach of children. Caution: This drug may cause low blood sugar and mental health-related side effects.\n" +
                        "Precaution: Antacids containing Calcium, Magnesium, or Aluminum, Sucralfate, and Multivitamins containing Zinc should not be taken within two hours before or after Ofloxacin administration.\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. CAUTION: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                        "Marketed By: INTAS Pharmaceuticals Ltd., Vadodara, Gujarat, India"
        );

        medData.put("Okamet-500",
                "Product Name: Okamet-500\n" +
                        "हिंदी में नाम: ओकामेट ५००\n" +
                        "Composition: Each uncoated tablet contains:\n" +
                        "• Metformin Hydrochloride IP: 500 mg\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Store in a cool, dry place. Keep out of reach of children.\n" +
                        "Declaration: SCHEDULE H PRESCRIPTION DRUG. CAUTION: It is dangerous to take this preparation except under medical supervision.\n" +
                        "Warning: To be sold by retail on the prescription of a Registered Medical Practitioner only.\n" +
                        "Manufacturing Information:\n" +
                        "• M.L. No.: L/17/2001/MNB\n" +
                        "• Manufactured by: Cipla Ltd.\n" +
                        "• Regd. Office: Nalagarh, Dist. Solan (H.P.)-174 101\n" +
                        "Registered Trademark: Cipla\n" +
                        "Metformin Hydrochloride Tablets IP 500 mg\n" +
                        "Batch Number: 21082962\n" +
                        "MRP: Rs. 27.60 (Inclusive of All Taxes)\n" +
                        "MFG. Date: Jan. 2024\n" +
                        "EXP. Date: Dec. 2026\n" +
                        "For: 20 Tablets"
        );

        medData.put("RANTAC 150",
                "Product Name: RANTAC® 150\n" +
                        "Composition: Each film coated tablet contains:\n" +
                        "• Ranitidine Hydrochloride IP equivalent to Ranitidine: 150 mg\n" +
                        "• Excipients: q.s.\n" +
                        "• Colors: Sunset Yellow FCF, Red Oxide of Iron, Titanium Dioxide IP\n" +
                        "Dosage: As directed by the Physician.\n" +
                        "Storage: Store in a cool, dry place. Protect from light & moisture.\n" +
                        "Manufacturing Information:\n" +
                        "• Manufactured in India by: J.B. CHEMICALS & PHARMACEUTICALS LTD., At Survey No. 101/2 & 102/1, Daman Industrial Estate, Kadaiya, Daman 396 210.\n" +
                        "• Mfg. Lic. No.: 134013\n" +
                        "Other Information:\n" +
                        "• Contains Ranitidine Tablets IP 150 mg\n" +
                        "• Schedule H Prescription Drug. Caution: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                        "• For: 30 Tablets"
        );

        medData.put("Saridon",
                "Product Name: Saridon\n" +
                        "Composition: Each uncoated tablet contains:\n" +
                        "• Paracetamol I.P.: 500 mg\n" +
                        "• Caffeine (Anhydrous) I.P.: 50 mg\n" +
                        "• Excipients: q.s.\n" +
                        "Dosage: 1 tablet every 4 to 6 hours, with a minimum gap of 4 hours and not more than 6 tablets per 24 hours.\n" +
                        "Storage: Keep in a cool and dark place away from light & moisture.\n" +
                        "Warning: Taking more than the daily dose may cause serious liver damage or allergic reactions (e.g., swelling of the face, mouth, and throat, difficulty in breathing, itching, or rash).\n" +
                        "Consult your doctor in case you are pregnant, lactating, or taking any other medication. Reduce intake of caffeine while taking this product.\n" +
                        "Not to be consumed with other paracetamol-containing medicines or if allergic to any of the ingredients.\n" +
                        "Net contents: 10 tablets\n" +
                        "Manufactured by: Piramal Pharma Limited\n" +
                        "• K-1, Addl, MIDC Area, Mahad - 402302, Maharashtra, India.\n" +
                        "• Mfg License No.: KD-187\n" +
                        "Marketed by: Bayer Pharmaceuticals Private Limited\n" +
                        "• Regd. Office: Bayer House, Central Avenue, Hiranandani Estate, Thane (West) - 400607, Maharashtra.\n" +
                        "Saridon is a Registered trademark of Bayer Consumer Care AG.\n" +
                        "Under License from: Bayer Consumer Care AG, Basel, Switzerland.\n" +
                        "Trademark Owners: Bayer Consumer Care AG\n" +
                        "For feedback/queries, contact Bayer India:\n" +
                        "• Call: 1800-22-2296\n" +
                        "• Email: [email protected]\n" +
                        "Batch Number: 22900410/02/2022\n" +
                        "Do not use for more than 5 days continuously.\n" +
                        "In case of excess pain, visit a physician. Bayer"
        );

        medData.put("Atchol-10", "Product Name: Atorvastatin 10 mg Tablet\n" +
                "Composition: Each film-coated tablet contains:\n" +
                "    Atorvastatin Calcium IP equivalent to Atorvastatin 10 mg\n" +
                "    Excipients\n" +
                "    Colours: Ferric Oxide NF Red and Titanium Dioxide IP\n" +
                "Dosage: As directed by the physician.\n" +
                "Warnings and Precautions:\n" +
                "    Contact a physician in case of any symptoms of liver problems like unusual fatigue, weakness, loss of appetite, upper belly pain, dark colored urine, yellowing of the skin or whites of the eyes, memory loss, confusion, and increased blood sugar.\n" +
                "    This is a Schedule H prescription drug. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                "Storage: Store protected from moisture at a temperature not exceeding 30°C. Keep out of reach of children.\n" +
                "Manufacturer: ARISTO Pharmaceuticals Private Limited, Plot Nos. 2040-46, N.H.-10, Baghey Khola, Po. Majhitar, East Sikkim - 737 136.\n"
        );


        medData.put("Acton-OR",
                "Tablet Type: Paracetamol Sustained Release Tablets\n" +
                        "Brand Name: Acton-OR\n" +
                        "Manufacturer: Apex Laboratories Private Limited\n" +
                        "Location: Alathur, Tamil Nadu, India\n" +
                        "Each uncoated bilayered tablet contains:\n" +
                        "Paracetamol LP 300 mg (As immediate release)\n" +
                        "Paracetamol LP 700 mg (As sustained release)\n" +
                        "Dosage: As directed by the Physician\n" +
                        "Directions for use: Do not chew or crush the tablet. Swallow as a whole.\n" +
                        "Maximum daily dose: 4000 mg\n" +
                        "Should not be used with other Paracetamol containing products.\n" +
                        "Warning: Overdose may be injurious to liver.\n" +
                        "Store at temperatures not exceeding 30°C.\n" +
                        "Protect from light and moisture.\n" +
                        "KEEP OUT OF REACH OF CHILDREN.\n" +
                        "Manufacturer: Apex Laboratories Private Limited, B-23 SIDCO Pharmaceutical Complex, Alathur-603110, Tamil Nadu, India"
        );

        medData.put("Azgil", "Product Name: Azgil 500 mg\n" +
                "Composition: Each film coated tablet contains:\n" +
                "Azithromycin IP equivalent to Anhydrous Azithromycin 500 mg\n" +
                "Colours: Yellow Oxide of Iron\n" +
                "Dosage: As directed by the Physician.\n" +
                "Storage: Store protected from light & moisture at a temperature not exceeding 30°C. Keep all medicines out of reach of children.\n" +
                "Manufacturer: Pure & Cure Healthcare Pvt. Ltd., Plot No. 26A, 27-30, Sector-8A, SIDCUL, Haridwar-249 403, Uttarakhand.\n");
        medData.put("Benz", "Product Name: Benz\n" +
                "Composition: Each soft gelatin capsule contains:\n" +
                "Benzonatate USP 100 mg\n" +
                "Excipients\n" +
                "Dosage: For adults and children 10 years of age and older: 1 capsule every 8 hours or as directed by the physician.\n" +
                "For children below 10 years of age: Consult a physician.\n" +
                "Warnings: Do not chew, swallow the capsule whole with water. Do not cut, chew or break the capsule before taking it. To be taken as per the recommended dose by the prescribing physician.\n" +
                "Storage: Store at 25°C, in a cool, dry, and dark place. Keep out of reach of children.\n" +
                "Manufacturer: Genova Laboratories (India) Pvt. Ltd., C-125, MIDC Area, Mahape, Navi Mumbai - 400 710, India");



        medData.put("Cipla", "Product Name: Ciplox 500 mg\n" +
                "Composition: Each film-coated tablet contains:\n" +
                "Ciprofloxacin Hydrochloride IP 500 mg\n" +
                "Colours: Titanium Dioxide IP\n" +
                "Dosage: As directed by the Physician.\n" +
                "Warnings: This drug may cause low blood sugar and mental health-related side effects.\n" +
                "Caution: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                "Storage: Protect from light and moisture. Keep out of reach of children.\n" +
                "Manufacturer: Cipla Ltd., Tarpin Block, Rorathang, Sikkim 737 133 India.");

        medData.put("Cyclopam", "Product Name: Cyclopam Fast Relief\n" +
                "Composition: Each uncoated tablet contains:\n" +
                "Dicyclomine Hydrochloride IP 20 mg\n" +
                "Paracetamol IP 500 mg\n" +
                "Dosage: As directed by the Physician.\n" +
                "Storage: Store in a cool and dry place. Protect from light and moisture.\n" +
                "Warnings: Taking more than 4000 mg of Paracetamol in 24 hours may cause serious liver damage or allergic reactions.\n"+
                "Caution: Not to be sold by retail without the prescription of a Registered Medical Practitioner.The drug should be used only under the supervision of a qualified doctor and should not be used with extreme caution with individuals and patients under the age of 6 months.\n" +
                "Manufacturer: Indoco Remedies Ltd., Village Katha, Baddi, Tehsil Nalagarh, Distt. Solan (HP) 173 205, Mumbai 400 098");

        medData.put("Dolo-650", "Product Name: Dolo-650\n" +
                "Composition: Each uncoated tablet contains:\n" +
                "Paracetamol IP 650 mg\n" +
                "Dosage: As directed by the Physician.\n" +
                "Storage: Store in a dry & dark place, at a temperature not exceeding 30°C. Overdose may be injurious to Liver.\n" +
                "Manufacturer: Micro Labs Limited, Mamring, Namthang Road, South Sikkim-737 132\n");
        medData.put("Emeset-8", "Product Name: Emeset-8\n" +
                "Composition: Each film-coated tablet contains:\n" +
                "Ondansetron Hydrochloride IP equivalent to Ondansetron 8 mg\n" +
                "Colour: Titanium Dioxide IP\n" +
                "Dosage: As directed by the Physician.\n" +
                "Warnings: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n" +
                "Storage: Store in a cool, dry place. Protect from light.\n" +
                "Manufacturer: Cipla Ltd., Tarpin Block, Rorathang, Sikkim 737 133 India.\n"
        );

        medData.put("Florita", "Composition:\n" +
                "Lactobacillus acidophilus USA 34 - 2 billion cfu\n" +
                "Lactobacillus rhamnosus 1853 - 2 billion cfu\n" +
                "Lactobacillus reuteri UBERO-87 - 2 billion cfu\n" +
                "Lactobacillus plantarum 68LP-4006 - 1 billion cfu\n" +
                "Lactobacillus casei 054642 - 1 billion cfu\n" +
                "Lactobacillus fermentum 0868-55 - 1 billion cfu\n" +
                "Bifidobacterium bifidum 08058 - 1 billion cfu\n" +
                "Total count: 10 billion cfu\n" +
                "Fructo Oligosaccharides (FOS) - 100 mg\n\n" +
                "Manufacturer: Unique Biotech Limited\n" +
                "Manufacturer Address: Plot No: 2, Phase-II, Kolthur, Shamirpet, Telangana, India - 500078\n" +
                "Storage: Store between 2°C to 8°C. Protect from light and moisture. Do not freeze.\n" +
                "Dosage: As directed by the physician.\n"
        );

        medData.put("Levoflox-500", "Composition:\n" +
                "Levofloxacin Hemihydrate IP equivalent to Levofloxacin 500 mg\n" +
                "Color: Red Oxide of Iron & Titanium Dioxide IP\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Protect from light & moisture. Keep out of reach of children.\n" +
                "Manufacturer: Cipla Ltd., Tarpin block, Rorathang, Sikkim 737 133 India.\n" +
                "Warning: This drug may cause low blood sugar and mental health-related side effects. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n");

        medData.put("Loparet", "Composition:\n" +
                "Loperamide Hydrochloride I.P. 2 mg\n" +
                "Excipients\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Store in a cool, dry & dark place.\n" +
                "Marketed by: RETORT LABORATORIES, 21/2 Mc Nichols Road, Chubar Chawl, Mumbai-600 001, India\n" +
                "Warning: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n");

        medData.put("Metrogyl 400", "Composition:\n" +
                "Metronidazole IP 400 mg\n" +
                "Excipients\n" +
                "Color: Sunset Yellow FCF\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Protect from light & moisture.\n" +
                "Manufacturer: J.B. CHEMICALS & PHARMACEUTICALS LTD., At: PF-23, GIDC Industrial Estate, Sanand-II, Ahmedabad-382 110.\n" +
                "Warning: SCHEDULE H PRESCRIPTION DRUG. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n");

        medData.put("MPTOL 500", "Composition:\n" +
                "Paracetamol IP 500 mg\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Store protected from light & moisture, at a temperature not exceeding 30°C. Keep out of reach of children.\n" +
                "Marketed By: MedePlus Health Services" +
                "Manufacturer: Pure & Cure Healthcare Pvt. Ltd. (A subsidiary of Akums Drugs & Pharmaceuticals Ltd.), Plot No. 26A, 27-30, Sector-8A, I.I.E., SIDCUL, Ranipur, Haridwar-249 403, Uttarakhand\n" +
                "Warning: CAUTION: Overdose of Paracetamol may be injurious to liver.\n"
        );

        medData.put("Nitroroin 100", "Composition:\n" +
                "Nitrofurantoin IP 100 mg\n" +
                "Excipients\n" +
                "Color: Titanium Dioxide\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Store in a cool, dry, and dark place. Protect from light and moisture.\n" +
                "Manufacturer: Morepen Laboratories Ltd., Plot No. 10, 11, Sector 68, I.E., SIDCUL, Haridwar-249403, Uttarakhand (INDIA)\n" +
                "Warning: SCHEDULE H PRESCRIPTION DRUG. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n");

        medData.put("Norflox-TZ RF", "Composition:\n" +
                "Norfloxacin IP 400 mg\n" +
                "Tinidazole IP 600 mg\n" +
                "Colors: Titanium Dioxide IP and Quinoline Yellow WS\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Store in a dry place at a temperature not exceeding 25°C. Keep out of reach of children.\n" +
                "Manufacturer: Cipla, Mid. under the technical guidance of Cipla Ltd. by Pinnacle Lite Science Private Limited, Khasra No. 1328-1330, Village Manpura, Tehsil-Baddi, Distt. Solan (HP)-174101, India\n" +
                "Warning: This drug may cause low blood sugar and mental health-related side effects. SCHEDULE PRESCRIPTION DRUG. Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n"
        );

        medData.put("Paracip-500", "Composition:\n" +
                "Paracetamol IP 500 mg\n" +
                "Dosage: Adults: 500 mg to 1000 mg (1 to 2 tablets) upto a maximum of 4000 mg daily in divided doses or as directed by the physician.\n" +
                "Storage: Store below 30°C. Protect from light & moisture. Keep out of reach of children.\n" +
                "Manufacturer: HSN Biotech, Plot No. 40, Sector 6A, SIDCUL, Haridwar-249 403, Uttarakhand\n" +
                "Marketed By: Cipla Ltd., Goregaon East, Mumbai-400 063, India\n");

        medData.put("Repace 50", "Composition:\n" +
                "Losartan Potassium IP 50 mg\n" +
                "Excipients\n" +
                "Color: Quinoline Yellow WS\n" +
                "Dosage: As directed by the Physician\n" +
                "Storage: Store at room temperature, protected from light and moisture.\n" +
                "Manufacturer: Suo Pharma Laboratories Ltd., Plot No. 107-108, Namli Block, Panipool, East-Sikkim-737 135.\n" +
                "Warning: SCHEDULE H PRESCRIPTION DRUG - CAUTION: Not to be sold by retail without the prescription of a Registered Medical Practitioner.\n");
        return view;
    }

    private void classifyImage(Bitmap image) {
        showToast("Extracting text...");
        progressBar.setVisibility(View.VISIBLE);

        // Convert the Bitmap image to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        SafetySetting dangerousSafety = new SafetySetting(HarmCategory.DANGEROUS_CONTENT,
                BlockThreshold.NONE);
        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.NONE);

        SafetySetting hateSpeechSafety = new SafetySetting(HarmCategory.HATE_SPEECH,
                BlockThreshold.NONE);
        // Initialize the generative model with the specified safety settings
        // Initialize the Gemini API model
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyCVESdoEoPK1xWlJFJiw3j2JrIbOrps_I4",null, Arrays.asList(harassmentSafety, hateSpeechSafety,dangerousSafety));
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Prepare the content with the image bytes
        Content content = new Content.Builder()
                .addImage(image)
                .addText("Please follow these instructions carefully:\n" +
                        "\n" +
                        "Extract Text from Image: Begin by thoroughly extracting any relevant text from the image provided.\n" +
                        "Identify Medicine Name: Using the extracted text and cross-referencing it with the dataset"+medData+", identify the exact medicine name. Ensure the name is provided in ALL CAPS.\n" +
                        "Response Format:\n" +
                        "\n" +
                        "Please respond with the following information:\n" +
                        "\n" +
                        "Medicine Name: <Exact Medicine Name in ALL CAPS>\n" +
                        "Detailed Description of Uses: Provide a concise yet comprehensive description of the medicine's primary uses, including its intended purpose, common conditions it treats, and any key effects or benefits.\n" +
                        "Example Response:\n" +
                        "\n" +
                        "Medicine Name: PARACETAMOL 500MG TABLET\n" +
                        "Detailed Description of Uses: Paracetamol is a widely used pain reliever and fever reducer. It’s commonly prescribed for mild to moderate pain, such as headaches, muscle aches, and dental pain, as well as for reducing fever in colds and flu. It works by inhibiting certain chemical messengers in the brain that cause pain and fever.\n" +
                        "Important Note: If the medicine cannot be identified in the dataset, or if the image content does not pertain to a medicine, please respond with: “Medicine not found or not applicable.")
                .build();

        // Call the API to extract text
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String extractedText = result.getText();
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);

                        // Display the extracted text in the chat or UI
                        Log.d(TAG, "onSuccess: fjfjfjfjfjfjjffj"+extractedText);
//                        addMessageToChat("Bot", extractedText);
                        ResultFragment resultFragment = ResultFragment.newInstance(extractedText);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, resultFragment) // Adjust container ID as needed
                                .addToBackStack(null) // Optional: to allow back navigation
                                .commit();
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);

                        Log.d(TAG, "erororororocoococ: "+t.toString());
                        // Handle failure
//                        addMessageToChat("Bot", "Error: " + t.toString());
                    });
                }
            }, getActivity().getMainExecutor());
        }
    }


    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == getActivity().RESULT_OK && data != null) {
            imageView.setImageURI(data.getData());
        }
    }
}
