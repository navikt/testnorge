import * as Yup from 'yup'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { InntektsaarForm } from './partials/inntektsaarForm'
import { ifPresent, requiredDate } from '@/utils/YupValidations'
import { useFormContext } from 'react-hook-form'
import './SigrunForm.less'

export const sigrunAttributt = 'sigrunstub'
export const SigrunstubForm = () => {
	const formMethods = useFormContext()
	return (
		<Vis attributt={sigrunAttributt}>
			<Panel
				heading="Lignet inntekt (Sigrun)"
				hasErrors={panelError(sigrunAttributt)}
				iconType="sigrun"
				startOpen={erForsteEllerTest(formMethods.getValues(), [sigrunAttributt])}
			>
				<InntektsaarForm formMethods={formMethods} />
			</Panel>
		</Vis>
	)
}

SigrunstubForm.validation = {
	sigrunstub: ifPresent(
		'$sigrunstub',
		Yup.array().of(
			Yup.object({
				grunnlag: Yup.array()
					.max(1)
					.of(
						Yup.object({
							tekniskNavn: Yup.string().required('Velg en type inntekt'),
							verdi: Yup.mixed().when('tekniskNavn', {
								is: 'skatteoppgjoersdato',
								then: () => requiredDate.nullable(),
								otherwise: () =>
									Yup.number()
										.min(0, 'Tast inn en gyldig verdi')
										.typeError('Tast inn en gyldig verdi'),
							}),
						}),
					)
					.test('is-required', 'Legg til minst én inntekt', (_val, context) => {
						const values = context.parent
						if (!values.tjeneste) {
							return true
						}
						return values.grunnlag?.length > 0 || values.svalbardGrunnlag?.length > 0
					}),
				inntektsaar: Yup.number()
					.min(2017)
					.integer('Ugyldig årstall')
					.required('Tast inn et gyldig år'),
				svalbardGrunnlag: Yup.array()
					.max(1)
					.of(
						Yup.object({
							tekniskNavn: Yup.string().required('Velg en type inntekt'),
							verdi: Yup.number()
								.min(0, 'Tast inn en gyldig verdi')
								.typeError('Tast inn en gyldig verdi'),
						}),
					),
				tjeneste: Yup.string().required('Velg en type tjeneste'),
			}),
		),
	),
}
