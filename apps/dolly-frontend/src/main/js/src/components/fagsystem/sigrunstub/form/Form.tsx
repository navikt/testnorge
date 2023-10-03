import * as Yup from 'yup'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { InntektsaarForm } from './partials/inntektsaarForm'
import { ifPresent, requiredDate } from '@/utils/YupValidations'

export const sigrunAttributt = 'sigrunstub'
export const SigrunstubForm = ({ formikBag }) => (
	<Vis attributt={sigrunAttributt}>
		<Panel
			heading="Lignet inntekt (Sigrun)"
			hasErrors={panelError(formikBag, sigrunAttributt)}
			iconType="sigrun"
			startOpen={erForsteEllerTest(formikBag.values, [sigrunAttributt])}
		>
			<InntektsaarForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

const mapTjeneste = (tjeneste) => {
	let mapped = tjeneste.toUpperCase()
	return mapped.replace(' ', '_')
}

SigrunstubForm.validation = {
	sigrunstub: ifPresent(
		'$sigrunstub',
		Yup.array().of(
			Yup.object({
				grunnlag: Yup.array()
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
					.test('is-required', 'Legg til minst én inntekt', function checkTjenesteGrunnlag(_val) {
						const values = this.options.context
						const path = this.options.path
						const index = path.charAt(path.indexOf('[') + 1)
						if (values.sigrunstub[index].tjeneste === 'BEREGNET_SKATT') {
							return values.sigrunstub[index].grunnlag.length > 0
						} else if (values.sigrunstub[index].tjeneste === 'SUMMERT_SKATTEGRUNNLAG') {
							return (
								values.sigrunstub[index].grunnlag.length > 0 ||
								values.sigrunstub[index].svalbardGrunnlag.length > 0
							)
						} else {
							return true
						}
					}),
				inntektsaar: Yup.number().integer('Ugyldig årstall').required('Tast inn et gyldig år'),
				svalbardGrunnlag: Yup.array().of(
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
