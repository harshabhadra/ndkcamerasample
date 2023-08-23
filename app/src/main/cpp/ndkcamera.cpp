//
// Created by harsh on 22-08-2023.
//

#include <camera/NdkCameraManager.h>
#include "ndkcamera.h"
#include <string>
#include "message-internal.h"


std::string NdkCamera::getBackFacingCamId(ACameraManager *cameraManager) {
    ACameraIdList *cameraIds = nullptr;
    ACameraManager_getCameraIdList(cameraManager, &cameraIds);

    std::string backId;

    for (int i = 0; i < cameraIds->numCameras; ++i) {
        const char *id = cameraIds->cameraIds[i];
        LOGE("Camera id: %s", id);
        ACameraMetadata *metadataObj;
        ACameraManager_getCameraCharacteristics(cameraManager, id, &metadataObj);

        ACameraMetadata_const_entry lensInfo = {0};
        camera_status_t status = ACameraMetadata_getConstEntry(metadataObj, ACAMERA_LENS_FACING,
                                                               &lensInfo);
        if (status == ACAMERA_OK) {
            auto facing = static_cast<acamera_metadata_enum_android_lens_facing_t>(
                    lensInfo.data.u8[0]);

            // Found a back-facing camera
            if (facing == ACAMERA_LENS_FACING_BACK) {
                backId = id;
                break;
            }
        }
    }

    ACameraManager_deleteCameraIdList(cameraIds);

    return backId;
}

NdkCamera::NdkCamera() = default;
