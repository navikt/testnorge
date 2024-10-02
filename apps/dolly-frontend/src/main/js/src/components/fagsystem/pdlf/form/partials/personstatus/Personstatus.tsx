import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'

type PersonstatusTypes = {
	path: string
}

export const PersonstatusForm = ({ path }: PersonstatusTypes) => {
	//TODO: Info om redigering av personstatus
	return (
		<>
			<FormSelect
				name={`${path}.status`}
				label="Personstatus"
				options={Options('personstatus')}
				isClearable={false}
			/>
			<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Personstatus gyldig f.o.m." />
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Personstatus gyldig t.o.m." />
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}
