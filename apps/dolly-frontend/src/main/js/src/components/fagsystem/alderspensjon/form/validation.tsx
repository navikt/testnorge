import {ifPresent, messages, requiredNumber} from '@/utils/YupValidations'
import * as Yup from 'yup'
import {isAfter} from 'date-fns'

export const validation = {
    alderspensjon: ifPresent(
        '$pensjonforvalter.alderspensjon',
        Yup.object({
            kravFremsattDato: Yup.date().nullable(),
            iverksettelsesdato: Yup.date()
                .when('soknad', {
                    is: (soknad: Boolean) => soknad,
                    then: () => Yup.date().test('is-month-after-now', 'Dato må være frem i tid.',
                        (iverksettelsesdato) => {
                            return iverksettelsesdato && isAfter(iverksettelsesdato, new Date())
                        }),
                    otherwise: () => Yup.date().test('is-month-after-kravfremsatt',
                        'Dato må være etter krav fremsatt.',
                        function validate(iverksettelsesdato) {
                            const kravFremsattDato: Date =
                                this.options.context?.pensjonforvalter?.alderspensjon?.kravFremsattDato
                            return iverksettelsesdato && isAfter(iverksettelsesdato, new Date(kravFremsattDato))
                        })
                })
                .nullable(),
            saksbehandler: Yup.string().nullable(),
            attesterer: Yup.string().nullable(),
            uttaksgrad: requiredNumber.typeError(messages.required),
            navEnhetId: Yup.string().nullable(),
            relasjoner: Yup.array().of(
                Yup.object({
                    sumAvForvArbKapPenInntekt: Yup.number()
                        .transform((i, j) => (j === '' ? null : i))
                        .nullable(),
                }),
            ),
        }),
    ),
}