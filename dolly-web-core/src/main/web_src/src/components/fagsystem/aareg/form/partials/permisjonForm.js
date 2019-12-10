import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

export const PermisjonForm = ({ name, formikBag, fjern }) => {
	const permisjonArray = _get(formikBag.values, name, [])
	const harPermisjon = permisjonArray.length > 0

	return (
		<div>
			{harPermisjon && (
				<div className="flexbox--align-center">
					<h4>Permisjon</h4>
					<HjelpeTekst>
						Start- og sluttdato må være innenfor perioden til arbeidsforholdet
					</HjelpeTekst>
				</div>
			)}
			{permisjonArray.map((permisjon, idx) => (
				<div key={idx} className="flexbox">
					<h5 className="nummer">{`#${idx + 1}`}</h5>
					<FormikSelect
						name={`${name}[${idx}].permisjonOgPermittering`}
						label="Permisjonstype"
						kodeverk="PermisjonsOgPermitteringsBeskrivelse"
					/>
					<FormikDatepicker name={`${name}[${idx}].permisjonsPeriode.fom`} label="Permisjon fra" />
					<FormikDatepicker name={`${name}[${idx}].permisjonsPeriode.tom`} label="Permisjon til" />
					<FormikTextInput
						name={`${name}[${idx}].permisjonsprosent`}
						label="Permisjonsprosent"
						type="number"
					/>
					<FieldArrayRemoveButton onClick={() => fjern(idx, name, permisjonArray)} />
				</div>
			))}
		</div>
	)
}
