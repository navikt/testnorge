import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as _ from 'lodash'
import { Medlemskapsperiode } from '@/components/fagsystem/medl/MedlTypes'

type MedlTypes = {
	data?: Medlemskapsperiode[]
}

const STATUS_AVVIST = 'AVST'
const STATUSAARSAK_FEILREGISTRERT = 'Feilregistrert'
const predicateGyldigeMedlemskapsperioder = () => (medlemskapsperiode: Medlemskapsperiode) =>
	medlemskapsperiode.status !== STATUS_AVVIST &&
	medlemskapsperiode.statusaarsak !== STATUSAARSAK_FEILREGISTRERT

const harGyldigMedlData = (data: Medlemskapsperiode[] | undefined) =>
	!_.isEmpty(data) && data?.some(predicateGyldigeMedlemskapsperioder())

export default ({ data }: MedlTypes) => {
	if (!harGyldigMedlData(data)) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Medlemskapsperioder" iconKind="calendar-days" />
			<DollyFieldArray
				data={data?.filter(predicateGyldigeMedlemskapsperioder())}
				header="Medlemskapsperiode"
				nested
			>
				{(medlemskapsperiode, idx) => {
					return (
						<div className="person-visning_content" key={idx}>
							<MedlVisning medlemskapsperiode={medlemskapsperiode} />
						</div>
					)
				}}
			</DollyFieldArray>
		</>
	)
}
