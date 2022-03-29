import React from 'react'
import _get from 'lodash/get'
import styled from 'styled-components'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

interface IdentifikasjonProps {
	formikBag: FormikProps<{}>
}

const SubTittel = styled.h4`
	margin: 10px 0 10px 0;
`

export const Identifikasjon: React.FC<IdentifikasjonProps> = ({
	formikBag,
}: IdentifikasjonProps) => {
	return (
		<section>
			<SubTittel>Identifikatortype</SubTittel>
			<FormikCheckbox
				name="personinformasjon.identifikasjon.identtype.fnr"
				disabled={_get(formikBag.values, 'personinformasjon.identifikasjon.identtype.dnr')}
				label="Fødselsnummer"
				size="medium"
			/>
			<FormikCheckbox
				name="personinformasjon.identifikasjon.identtype.dnr"
				disabled={_get(formikBag.values, 'personinformasjon.identifikasjon.identtype.fnr')}
				label="D-nummer"
				size="medium"
			/>
			<SubTittel>Adressebeskyttelse</SubTittel>
			<FormikSelect
				name="personinformasjon.identifikasjon.adressebeskyttelse"
				label="Gradering"
				options={[
					{ value: 'FORTROLIG', label: 'Fortrolig' },
					{ value: 'STRENGT_FORTROLIG', label: 'Strengt fortrolig' },
				]}
				size="medium"
			/>
			<SubTittel>Identitet</SubTittel>
			<FormikCheckbox
				name="personinformasjon.identifikasjon.falskIdentitet"
				label="Har falsk identitet"
				size="medium"
			/>
			<FormikCheckbox
				name="personinformasjon.identifikasjon.utenlandskIdentitet"
				label="Har utenlandsk identitet"
				size="medium"
			/>
		</section>
	)
}

export const IdentifikasjonPaths = {
	'personinformasjon.identifikasjon.falskIdentitet': 'boolean',
	'personinformasjon.identifikasjon.utenlandskIdentitet': 'boolean',
	'personinformasjon.identifikasjon.identtype.fnr': 'boolean',
	'personinformasjon.identifikasjon.identtype.dnr': 'boolean',
	'personinformasjon.identifikasjon.adressebeskyttelse': 'string',
}
