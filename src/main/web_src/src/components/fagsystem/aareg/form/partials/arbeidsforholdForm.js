import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { OrgnummerToggle } from './orgnummerToggle'
import { ArbeidKodeverk } from '~/config/kodeverk'
import Hjelpetekst from '~/components/hjelpetekst'

// export const ArbeidsforholdForm = ({ path, formikBag, brukerId }) => {
export const ArbeidsforholdForm = ({ path, formikBag, erLenket, brukerId }) => {
	// const erLenket = window.localStorage.getItem('erLenket')
	const arbeidsforhold = _get(formikBag.values, path)

	console.log('erLenket 2:>> ', erLenket)

	const arbeidsforholdIndex = path.charAt(path.length - 1)

	const virksomheter = SelectOptionsOppslag.hentVirksomheterFraOrgforvalter(brukerId)
	const virksomheterOptions = SelectOptionsOppslag.formatOptions('virksomheter', virksomheter)

	const onChangeLenket = (field, fieldPath) => {
		const amelding = _get(formikBag.values, 'aareg[0].amelding')
		// console.log('`${fieldPath}.${path}`, :>> ', `${path}.${fieldPath}`)
		console.log('erLenket 3:>> ', erLenket)
		if (erLenket) {
			amelding.forEach((maaned, idx) => {
				formikBag.setFieldValue(
					`aareg[0].amelding[${idx}].arbeidsforhold[${arbeidsforholdIndex}].${fieldPath}`,
					field.value
				)
			})
		} else {
			formikBag.setFieldValue(`${path}.${fieldPath}`, field.value)
		}
	}
	// console.log('arbeidsforhold :>> ', arbeidsforhold)
	// console.log('path :>> ', path)

	// const clearOrgnrIdent = aktoer => {
	// 	formikBag.setFieldValue(`${path}.arbeidsgiver.aktoertype`, aktoer.value)
	// 	formikBag.setFieldValue(`${path}.arbeidsgiver.orgnummer`, '')
	// 	formikBag.setFieldValue(`${path}.arbeidsgiver.ident`, '')
	// }

	// console.log('path :>> ', path)
	// console.log(
	// 	'_get(formikBag.values:>> ',
	// 	_get(formikBag.values, 'aareg[0].ansettelsesPeriode.tom')
	// )

	// console.log(
	// 	'get(formikBag.values, `${path}.ansettelsesPeriode.tom`)  :>> ',
	// 	_get(formikBag.values, `${path}.ansettelsesPeriode.tom`)
	// )

	// console.log('path :>> ', path)

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.arbeidsgiver.orgnummer`}
					label="Organisasjonsnummer"
					//TODO: Laster ikke med en gang
					options={virksomheterOptions}
					size="xxlarge"
					isClearable={false}
				/>
				{/* //TODO arbeidsforholdId blir ikke oppdatert */}
				<FormikTextInput name={`${path}.arbeidsforholdId`} label="Arbeidsforhold-ID" type="text" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.fom`} label="Ansatt fra" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.tom`} label="Ansatt til" />
				<FormikSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					onChange={field => onChangeLenket(field, 'ansettelsesPeriode.sluttaarsak')}
					disabled={
						_get(formikBag.values, `${path}.ansettelsesPeriode.tom`) === null ? true : false
					}
					// TODO disabled funker ikke!
				/>
				{/* <FormikSelect
					name={`${path}.arbeidsforholdstype`}
					label="Type arbeidsforhold"
					kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
					size="large"
					isClearable={false}
				/> */}
				{/* <FormikSelect
					name={`${path}.arbeidsgiver.aktoertype`}
					label="Type arbeidsgiver"
					options={Options('aktoertype')}
					onChange={clearOrgnrIdent}
					size="medium"
					isClearable={false}
				/> */}
				{/* {arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
					<FormikTextInput name={`${path}.arbeidsgiver.ident`} label="Arbeidsgiver ident" />
				)} */}
			</div>
			{/* {arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
				<OrgnummerToggle formikBag={formikBag} path={`${path}.arbeidsgiver.orgnummer`} />
			)} */}

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />

			{/* <TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} /> */}
		</React.Fragment>
	)
}
