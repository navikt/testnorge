import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

type PersonstatusTypes = {
	path: string
}

export const PersonstatusForm = ({ path }: PersonstatusTypes) => {
	return (
		<>
			<div className="flexbox--align-center">
				<FormSelect
					name={`${path}.status`}
					label="Personstatus"
					options={Options('personstatus')}
					isClearable={false}
				/>
				<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Personstatus gyldig f.o.m." />
				<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Personstatus gyldig t.o.m." />
				<div style={{ marginLeft: '-15px' }}>
					<Hjelpetekst>
						Endring av personstatus er kun ment for negativ testing. Adresser og andre avhengige
						verdier vil ikke bli oppdatert for å stemme overens med ny personstatus.
					</Hjelpetekst>
				</div>
			</div>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}
