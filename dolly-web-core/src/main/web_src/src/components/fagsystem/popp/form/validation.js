import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { requiredNumber, ifPresent} from '~/utils/YupValidations'

const innenforInntektsperiodeTest = (validation, validateFomBasedOnAge) =>{
    const errorMsg = 'T.o.m. dato kan ikke være før f.o.m. dato.'
    const errorMsgAge =
        'F.o.m. dato må være før t.o.m. dato, og kan tidligst være året personen fyller 18.'
    return validation.test(
        'range',
        validateFomBasedOnAge ? errorMsgAge : errorMsg,
        function isWithinTest(val) {
            if (!val) return true

            const dateValue = val
            const path = this.path
            const values = this.options.context
            const arrayPos = path.split('.')[0] // feks: popp[1]

            if(validateFomBasedOnAge){
                const inntektFom = _get(values, `${arrayPos}.inntektsperiode.fom`)

                const alder = _get(values, 'tpsf.alder')
                const foedtFoer = _get(values, 'tpsf.foedtFoer')


                if(!_isNil(alder)){
                    if(new Date().getFullYear() - alder + 18 > inntektFom ){
                        return false
                    }
                }else if(!_isNil(foedtFoer)){
                    let day = foedtFoer.getDate()
                    let month = foedtFoer.getMonth()
                    let year = foedtFoer.getFullYear()

                    year = (day==1 && month==0) ?  year - 1 : year

                    if(year + 18 > inntektFom){
                        return false
                    }
                }else{
                    if(new Date().getFullYear() - 12 > inntektFom){
                        return false
                    }
                }
            }

            const inntektFom = _get(values, `${arrayPos}.inntektsperiode.fom`)
            const inntektTom = _get(values, `${arrayPos}.inntektsperiode.tom`)

            return dateValue >= inntektFom && dateValue <= (_isNil(inntektTom) ? new Date().getFullYear() : inntektTom)
        }
    )
}

export const validation  = {
    popp: ifPresent(
        '$popp',
        Yup.array().of(
            Yup.object({
                inntektsperiode: Yup.object({
                    fom: innenforInntektsperiodeTest(requiredNumber, true),
                    tom: innenforInntektsperiodeTest(Yup.number().nullable())
                }),
                beloep: Yup.number()
                    .min(0, 'Tast inn et gyldig beløp')
                    .typeError('Tast inn et gyldig beløp')
            })
        )
    )
}