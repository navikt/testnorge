import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermisjon } from '../initialValues'

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermisjonForm = ({ path }) => (
	<DollyFieldArray name={path} title="Permisjon" infotekst={infotekst} newEntry={initialPermisjon}>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikSelect
					name={`${path}.permisjonOgPermittering`}
					label="Permisjonstype"
					kodeverk="PermisjonsOgPermitteringsBeskrivelse"
					isClearable={false}
				/>
				<FormikDatepicker name={`${path}.permisjonsPeriode.fom`} label="Permisjon fra" />
				<FormikDatepicker name={`${path}.permisjonsPeriode.tom`} label="Permisjon til" />
				<FormikTextInput
					name={`${path}.permisjonsprosent`}
					label="Permisjonsprosent"
					type="number"
				/>
			</React.Fragment>
		)}
	</DollyFieldArray>
)
