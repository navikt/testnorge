import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const initialValues = {
	startAarMaaned: '',
	antallMaaneder: null,
	opplysningspliktig: '',
	virksomhet: Options('orgnummer')[Math.floor(Math.random() * Options('orgnummer').length)].value,
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
						<FormikTextInput
							name={`${path}.opplysningspliktig`}
							label="Opplysningspliktig (orgnr/id)"
							fastfield={false}
						/>
						<FormikSelect
							name={`${path}.virksomhet`}
							label="Virksomhet (orgnr/id)"
							options={Options('orgnummer')}
							type="text"
							size="large"
							isClearable={false}
						/>
						{/* <FormikTextInput
							name={`${path}.virksomhet`}
							label="Virksomhet (orgnr/id)"
							fastfield={false}
						/> */}
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
