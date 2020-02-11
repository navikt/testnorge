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
import { OrgnrForm } from './orgnrForm'

export const ArbeidsforholdForm = ({ path, formikBag }) => {
	const arbeidsforhold = _get(formikBag.values, path)

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
					size="medium"
					isClearable={false}
				/>
				{arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
					<FormikTextInput name={`${path}.arbeidsgiver.ident`} label="Arbeidsgiver ident" />
				)}
			</div>
			{arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
				<OrgnrForm path={path} formikBag={formikBag} />
			)}

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />

			<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />
		</React.Fragment>
	)
}
