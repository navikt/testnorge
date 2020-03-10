import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

const initialValues = {
	startAarMaaned: '',
	antallMaaneder: null,
	virksomhet: '',
	opplysningspliktig: '',
	inntektsliste: [
		{
			beloep: null,
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
	'For å generere samme inntektsinformasjon for flere måneder - fyll inn første måned/år, samt antall måneder inntektsinformasjonen skal genereres for.'

export const InntektsinformasjonForm = ({ formikBag }) => {
	const orgInfo = SelectOptionsOppslag('orgnr')
	const options = SelectOptionsOppslag.formatOptions(orgInfo)
	const randomNumber = Math.floor(Math.random() * options.length)
	initialValues.virksomhet = options.length > 0 && options[randomNumber].value
	initialValues.opplysningspliktig = options.length > 0 && options[randomNumber].juridiskEnhet

	const setOrgnummer = (org, path) => {
		formikBag.setFieldValue(`${path}.virksomhet`, org.value)
		formikBag.setFieldValue(`${path}.opplysningspliktig`, org.juridiskEnhet)
	}

	return (
		<FormikDollyFieldArray
			name="inntektstub.inntektsinformasjon"
			title="Inntektsinformasjon"
			hjelpetekst={infotekst}
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					<div className="flexbox--flex-wrap">
						<FormikTextInput name={`${path}.startAarMaaned`} label="Start måned/år" type="month" />
						<FormikTextInput name={`${path}.antallMaaneder`} label="Antall måneder" type="number" />
						<DollySelect
							name={`${path}.virksomhet`}
							label="Virksomhet (orgnr/id)"
							isLoading={orgInfo.loading}
							options={options}
							size="xlarge"
							onChange={org => setOrgnummer(org, path)}
							value={_get(formikBag.values, `${path}.virksomhet`)}
							isClearable={false}
						/>
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
