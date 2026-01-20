import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import InntektsinformasjonForm from './partials/inntektsinformasjonForm'
import { Inntektsinformasjon } from './partials/inntektstubTypes'
import { useFormContext } from 'react-hook-form'

export const initialValues: Inntektsinformasjon = {
	sisteAarMaaned: '',
	rapporteringsdato: null,
	antallMaaneder: '',
	virksomhet: '',
	opplysningspliktig: '',
	versjon: null,
	inntektsliste: [
		{
			beloep: '',
			startOpptjeningsperiode: undefined,
			sluttOpptjeningsperiode: undefined,
			inntektstype: '',
		},
	],
	fradragsliste: [],
	forskuddstrekksliste: [],
}

export const inntektstubAttributt = 'inntektstub'
const inntektstubPath = 'inntektstub.inntektsinformasjon'

const infotekst =
	'For å generere samme inntektsinformasjon for flere måneder - fyll inn siste måned/år, samt antall måneder bakover inntektsinformasjonen skal genereres for.'

export const InntektstubForm = () => {
	const formMethods = useFormContext()
	return (
		//@ts-ignore
		<Vis attributt={inntektstubAttributt}>
			<Panel
				heading="A-ordningen (Inntektstub)"
				hasErrors={panelError(inntektstubAttributt)}
				iconType="inntektstub"
				startOpen={erForsteEllerTest(formMethods.getValues(), [inntektstubAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<FormDollyFieldArray
						tag={undefined}
						name={inntektstubPath}
						header="Inntektsinformasjon"
						newEntry={initialValues}
						hjelpetekst={infotekst}
						canBeEmpty={false}
					>
						{(path: string) => <InntektsinformasjonForm path={path} />}
					</FormDollyFieldArray>
				</div>
			</Panel>
		</Vis>
	)
}

InntektstubForm.validation = validation
