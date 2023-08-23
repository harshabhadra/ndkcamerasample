//
// Created by harsh on 22-08-2023.
//
#include <string>
#include <camera/NdkCameraManager.h>
#ifndef OPENCVTEST2_NDKCAMERA_H
#define OPENCVTEST2_NDKCAMERA_H

class NdkCamera{
public:NdkCamera();
    static std::string getBackFacingCamId(ACameraManager *cameraManager);
};
#endif //OPENCVTEST2_NDKCAMERA_H
