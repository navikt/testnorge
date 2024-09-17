import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { FullmaktTypes } from '@/components/fagsystem/fullmakt/FullmaktTypes'

type FullmaktProps = {
	data: FullmaktTypes
}

export const FullmaktVisning = ({ data }: FullmaktProps) => {
	if (!data?.fullmaktsgiver) {
		return null
	}
	//TODO: Legge til flere verdier
	return (
		<>
			<TitleValue title="Fullmaktsgiver" value={data.fullmaktsgiver} />
			<TitleValue title="Gyldig fra" value={formatDate(data.gyldigFraOgMed)} />
			<TitleValue title="Gyldig til" value={formatDate(data.gyldigTilOgMed)} />
		</>
	)
}
