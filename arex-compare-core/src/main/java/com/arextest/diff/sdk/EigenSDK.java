package com.arextest.diff.sdk;

import com.arextest.diff.model.eigen.EigenOptions;
import com.arextest.diff.model.eigen.EigenResult;
import java.util.Collections;

public class EigenSDK {

  public EigenResult calculateEigen(String msg) {
    EigenResult eigenResult = new EigenResult();
    eigenResult.setEigenMap(Collections.emptyMap());
    return eigenResult;
  }

  public EigenResult calculateEigen(String msg, EigenOptions eigenOptions) {
    EigenResult eigenResult = new EigenResult();
    eigenResult.setEigenMap(Collections.emptyMap());
    return eigenResult;
  }
}
