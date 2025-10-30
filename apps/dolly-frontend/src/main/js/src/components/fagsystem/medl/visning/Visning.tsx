import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as _ from 'lodash-es'
import { Medlemskapsperiode } from '@/components/fagsystem/medl/MedlTypes'
import styled from 'styled-components'
import { getFagsystemTimeoutTitle } from '@/components/fagsystem/utils'

type MedlTypes = {
	data?: Medlemskapsperiode[]
	timedOutFagsystemer?: string[]
}

const StyledSuboverskrift = styled(SubOverskrift)`
	margin-bottom: -10px;
	padding-bottom: -10px;
`

const STATUS_AVVIST = 'AVST'
const STATUSAARSAK_FEILREGISTRERT = 'Feilregistrert'
const predicateGyldigeMedlemskapsperioder = () => (medlemskapsperiode: Medlemskapsperiode) =>
	medlemskapsperiode.status !== STATUS_AVVIST &&
	medlemskapsperiode.statusaarsak !== STATUSAARSAK_FEILREGISTRERT

const harGyldigMedlData = (data: Medlemskapsperiode[] | undefined) =>
	!_.isEmpty(data) && data?.some(predicateGyldigeMedlemskapsperioder())

export default ({ data, timedOutFagsystemer = [] }: MedlTypes) => {
	if (!harGyldigMedlData(data)) {
		return null
	}

	return (
		<>
			<StyledSuboverskrift
				label="Medlemskap"
				iconKind="calendar"
				style={{ marginBottom: '-2px' }}
				title={timedOutFagsystemer?.includes('MEDL') ? getFagsystemTimeoutTitle('MEDL') : undefined}
			/>
			<DollyFieldArray
				data={data?.filter(predicateGyldigeMedlemskapsperioder())}
				header="Medlemskapsperiode"
				nested
			>
				{(medlemskapsperiode: Medlemskapsperiode) => (
					<MedlVisning medlemskapsperiode={medlemskapsperiode} />
				)}
			</DollyFieldArray>
		</>
	)
}
