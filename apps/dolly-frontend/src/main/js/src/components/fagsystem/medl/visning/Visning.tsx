import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as _ from 'lodash'
import { Medlemskapsperioder } from '@/components/fagsystem/medl/MedlTypes'

type MedlTypes = {
	data?: Medlemskapsperioder[]
}

export default ({ data }: MedlTypes) => {
	if (_.isEmpty(data)) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Medlemskapsperioder" iconKind="calendar" />
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
