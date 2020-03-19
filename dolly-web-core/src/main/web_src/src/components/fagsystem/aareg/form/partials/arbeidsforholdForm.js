import React, { useState, useEffect } from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const ArbeidsforholdForm = ({ path, formikBag }) => {
	const [loading, setLoading] = useState(true)
	const [options, setOptions] = useState([])

	useEffect(() => {
		const organisasjoner = []
		SelectOptionsOppslag.hentOrgnr()
			.then(response => {
				response.liste.forEach(org => {
					org.juridiskEnhet &&
						organisasjoner.push({
							value: org.orgnr,
							label: `${org.orgnr} (${org.enhetstype}) - ${org.navn}`
						})
				})
			})
			.then(() => setOptions(organisasjoner))
			.then(() => setLoading(false))
	}, [])

	const arbeidsforhold = _get(formikBag.values, path)

	const clearOrgnrIdent = aktoer => {
		formikBag.setFieldValue(`${path}.arbeidsgiver.aktoertype`, aktoer.value)
		formikBag.setFieldValue(`${path}.arbeidsgiver.orgnummer`, '')
		formikBag.setFieldValue(`${path}.arbeidsgiver.ident`, '')
	}

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				<FormikDatepicker name={`${path}.ansettelsesPeriode.fom`} label="Ansatt fra" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.tom`} label="Ansatt til" />
				<FormikSelect
					name={`${path}.arbeidsforholdstype`}
					label="Type arbeidsforhold"
					kodeverk="Arbeidsforholdstyper"
					size="large"
					isClearable={false}
				/>
				<FormikSelect
					name={`${path}.arbeidsgiver.aktoertype`}
					label="Type arbeidsgiver"
					options={Options('aktoertype')}
					onChange={clearOrgnrIdent}
					size="medium"
					isClearable={false}
				/>
				{arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
					<FormikTextInput name={`${path}.arbeidsgiver.ident`} label="Arbeidsgiver ident" />
				)}
				{arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
					<FormikSelect
						name={`${path}.arbeidsgiver.orgnummer`}
						label="Organisasjonsnummer"
						isLoading={loading}
						options={options}
						type="text"
						size="xlarge"
						isClearable={false}
					/>
				)}
			</div>

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />

			<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />
		</React.Fragment>
	)
}
