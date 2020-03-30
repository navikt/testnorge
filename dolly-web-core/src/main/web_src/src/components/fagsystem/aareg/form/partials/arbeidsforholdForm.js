import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { OrgnummerToggle } from './orgnummerToggle'
import { ArbeidKodeverk } from '~/config/kodeverk'
import Hjelpetekst from '~/components/hjelpetekst'

export const ArbeidsforholdForm = ({ path, formikBag }) => {
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
					kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
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
			</div>
			{arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
				<>
					<FormikTextInput
						name={`${path}.arbeidsgiver.orgnummer`}
						label="Organisasjonsnummer"
						size="medium"
					/>
					<Hjelpetekst hjelpetekstFor="Skriv inn orgnr">
						De syntetiske organisasjonsnummerne er midlertidig fjernet. Vi jobber med å gjøre
						løsningen mer stabil. Hvis du sliter med å finne et orgnr gjennom Ereg, ta kontakt med
						oss på #dolly.
					</Hjelpetekst>
				</>
				// Midlertidig fjerning av nedtrekksliste med syntetiske orgnr
				// <OrgnummerToggle formikBag={formikBag} path={`${path}.arbeidsgiver.orgnummer`} />
			)}

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />

			<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />
		</React.Fragment>
	)
}
