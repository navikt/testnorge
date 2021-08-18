import React from 'react'
import { FormikProps } from 'formik'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import InntektsinformasjonForm from './partials/inntektsinformasjonForm'
import { Inntektsinformasjon } from './partials/inntektstubTypes'

interface InntektstubForm {
	formikBag: FormikProps<{}>
}

export const initialValues: Inntektsinformasjon = {
	sisteAarMaaned: '',
	antallMaaneder: '',
	virksomhet: '',
	opplysningspliktig: '',
	versjon: null,
	inntektsliste: [
		{
			beloep: '',
			startOpptjeningsperiode: undefined,
			sluttOpptjeningsperiode: undefined,
			inntektstype: ''
		}
	],
	fradragsliste: [],
	forskuddstrekksliste: [],
	arbeidsforholdsliste: []
}

const inntektstubAttributt = 'inntektstub'
const inntektstubPath = 'inntektstub.inntektsinformasjon'

const infotekst =
	'For å generere samme inntektsinformasjon for flere måneder - fyll inn siste måned/år, samt antall måneder bakover inntektsinformasjonen skal genereres for.'

export const InntektstubForm = ({ formikBag }: InntektstubForm) => (
	//@ts-ignore
	<Vis attributt={inntektstubAttributt}>
		<Panel
			heading="A-ordningen (Inntektskomponenten)"
			hasErrors={panelError(formikBag, inntektstubAttributt)}
			iconType="inntektstub"
			//@ts-ignore
			startOpen={() => erForste(formikBag.values, [inntektstubAttributt])}
		>
			<div className="flexbox--flex-wrap">
				<FormikDollyFieldArray
					tag={undefined}
					name={inntektstubPath}
					header="Inntektsinformasjon"
					newEntry={initialValues}
					hjelpetekst={infotekst}
					canBeEmpty={false}
				>
					{(path: string) => <InntektsinformasjonForm path={path} formikBag={formikBag} />}
				</FormikDollyFieldArray>
			</div>
		</Panel>
	</Vis>
)

InntektstubForm.validation = validation
