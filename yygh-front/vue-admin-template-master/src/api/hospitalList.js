import request from '@/utils/request'

export default{
  getHospitalSetList(current,limit,searchObj){
    return request({
      url: `/admin/hosp/hospitalSet/findPage/${current}/${limit}`,
      method: 'post',
      data: searchObj
    })
  }
}
