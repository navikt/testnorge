import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DatepickerWrapper } from '~/components/ui/form/inputs/datepicker/DatepickerStyled'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'

export const PersonstatusForm = ({ path }) => {
	return (
		<>
			<FormikSelect
				name={`${path}.status`}
				label="Personstatus"
				options={Options('personstatus')}
			/>
			<DatepickerWrapper>
				<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Personstatus gyldig f.o.m." />
				<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Personstatus gyldig t.o.m." />
			</DatepickerWrapper>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}
