import { ifPresent, messages, requiredNumber } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { isAfter } from 'date-fns'

export const validation = {
	alderspensjon: ifPresent(
		'$pensjonforvalter.alderspensjon',
		Yup.object({
			kravFremsattDato: Yup.date().nullable(),
			iverksettelsesdato: Yup.date()
				.test(
					'er-tid-fram',
					'Måned må etter være etter krav fremsatt dato',
					function validDate(iverksettelsesdato) {
						const kravFremsattDato =
							this.options.context?.pensjonforvalter?.alderspensjon?.kravFremsattDato
						return isAfter(new Date(iverksettelsesdato), new Date(kravFremsattDato))
					},
				)
				.nullable(),
			saksbehandler: Yup.string().nullable(),
			attesterer: Yup.string().nullable(),
			uttaksgrad: requiredNumber.typeError(messages.required),
			navEnhetId: Yup.string().nullable(),
		}),
	),
}
