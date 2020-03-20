import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { OrgnummerForm } from '~/components/fagsystem/aareg/form/partials/orgnummerForm'

const initialValues = {
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

export const InntektsinformasjonForm = ({ formikBag }) => {
	return (
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
						<OrgnummerForm path={path} formikBag={formikBag} type="inntektstub" />
					</div>
					<InntektForm formikBag={formikBag} inntektsinformasjonPath={path} />
					<FradragForm formikBag={formikBag} inntektsinformasjonPath={path} />
					<ForskuddstrekkForm formikBag={formikBag} inntektsinformasjonPath={path} />
					<ArbeidsforholdForm formikBag={formikBag} inntektsinformasjonPath={path} />
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
