import React from 'react'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubOrgnummerSelect } from './partials/inntektstubOrgnummerSelect'
import { InntektForm } from './partials/inntektForm'
import { FradragForm } from './partials/fradragForm'
import { ForskuddstrekkForm } from './partials/forskuddstrekkForm'
import { ArbeidsforholdForm } from './partials/arbeidsforholdForm'

export const initialValues = {
	sisteAarMaaned: '',
	antallMaaneder: '',
	virksomhet: '',
	opplysningspliktig: '',
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

const infotekst =
	'For å generere samme inntektsinformasjon for flere måneder - fyll inn siste måned/år, samt antall måneder bakover inntektsinformasjonen skal genereres for.'

const inntektstubAttributt = 'inntektstub'

export const InntektstubForm = ({ formikBag }) => (
	<Vis attributt={inntektstubAttributt}>
		<Panel
			heading="A-ordningen (Inntektskomponenten)"
			hasErrors={panelError(formikBag, inntektstubAttributt)}
			iconType="inntektstub"
			startOpen={() => erForste(formikBag.values, [inntektstubAttributt])}
		>
			<div className="flexbox--flex-wrap">
				<FormikDollyFieldArray
					name="inntektstub.inntektsinformasjon"
					header="Inntektsinformasjon"
					hjelpetekst={infotekst}
					newEntry={initialValues}
				>
					{path => (
						<React.Fragment>
							<div className="flexbox--flex-wrap">
								<FormikTextInput name={`${path}.sisteAarMaaned`} label="Måned/år" type="month" />
								<FormikTextInput
									name={`${path}.antallMaaneder`}
									label="Generer antall måneder"
									type="number"
								/>
								<InntektstubOrgnummerSelect path={path} formikBag={formikBag} />
							</div>
							<InntektForm formikBag={formikBag} inntektsinformasjonPath={path} />
							<FradragForm formikBag={formikBag} inntektsinformasjonPath={path} />
							<ForskuddstrekkForm formikBag={formikBag} inntektsinformasjonPath={path} />
							<ArbeidsforholdForm formikBag={formikBag} inntektsinformasjonPath={path} />
						</React.Fragment>
					)}
				</FormikDollyFieldArray>
			</div>
		</Panel>
	</Vis>
)

InntektstubForm.validation = validation
