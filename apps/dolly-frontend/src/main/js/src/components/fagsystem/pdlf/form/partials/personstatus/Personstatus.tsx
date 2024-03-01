import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'

type PersonstatusTypes = {
	path: string
}

export const PersonstatusForm = ({ path }: PersonstatusTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.status`}
				label="Personstatus"
				options={Options('personstatus')}
				isClearable={false}
			/>
			<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Personstatus gyldig f.o.m." />
			<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Personstatus gyldig t.o.m." />
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}
