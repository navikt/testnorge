import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { requiredNumber, ifPresent} from '~/utils/YupValidations'

const innenforInntektsdatoerTest = (validation) =>{
    const errorMsg = 'Tast inn et gyldig år.'
    return validation.test(
        'range',
        errorMsg,
        function isWithinTest(val) {
            if (!val) return true

            const dateValue = val
            const path = this.path
            const values = this.options.context
            const arrayPos = path.split('.')[0] // feks: popp[1]

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
                    fom: innenforInntektsdatoerTest(requiredNumber),
                    tom: Yup.number().nullable()
                }),
                beloep: Yup.number()
                    .min(0, 'Tast inn et gyldig beløp')
                    .typeError('Tast inn et gyldig beløp')
            })
        )
    )
}