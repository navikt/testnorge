import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as _ from 'lodash'
import { Medlemskapsperioder } from '@/components/fagsystem/medl/MedlTypes'

type MedlTypes = {
	data?: Medlemskapsperioder[]
}

function harGyldigMedlData(data: Medlemskapsperioder[] | undefined) {
	return (
		!_.isEmpty(data) &&
		data?.some(
			(medlemskapsperiode) =>
				medlemskapsperiode.status !== 'AVST' && medlemskapsperiode.statusaarsak !== 'Feilregistrert'
		)
	)
}

export default ({ data }: MedlTypes) => {
	if (!harGyldigMedlData(data)) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Medlemskapsperioder" iconKind="calendar-days" />
			<DollyFieldArray data={data} header="Medlemskapsperiode" nested>
				{(medlemskap, idx) => {
					return (
						<div className="person-visning_content" key={idx}>
							<MedlVisning medlemskapsperiode={medlemskap} />
						</div>
					)
				}}
			</DollyFieldArray>
		</>
	)
}
