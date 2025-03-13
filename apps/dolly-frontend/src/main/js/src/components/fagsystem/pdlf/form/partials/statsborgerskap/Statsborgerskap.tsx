import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialStatsborgerskap } from '@/components/fagsystem/pdlf/form/initialValues'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

type StatsborgerskapTypes = {
	path: string
	kanVelgeMaster?: boolean
}

export const StatsborgerskapForm = ({ path, kanVelgeMaster }: StatsborgerskapTypes) => {
	return (
		<>
			<FormSelect
				name={`${path}.landkode`}
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				size="large"
				isClearable={false}
			/>
			<FormDatepicker
				name={`${path}.gyldigFraOgMed`}
				label="Statsborgerskap fra"
				maxDate={new Date()}
			/>
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Statsborgerskap til" />
			<AvansertForm path={path} kanVelgeMaster={kanVelgeMaster} />
		</>
	)
}

export const Statsborgerskap = () => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.statsborgerskap'}
				header="Statsborgerskap"
				newEntry={getInitialStatsborgerskap(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => (
					<StatsborgerskapForm
						path={path}
						kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
					/>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
