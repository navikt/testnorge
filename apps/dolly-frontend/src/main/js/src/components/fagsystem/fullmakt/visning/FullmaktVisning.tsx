import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { FullmaktTypes } from '@/components/fagsystem/fullmakt/FullmaktTypes'
import { useFullmektig } from '@/utils/hooks/useFullmakt'

type FullmaktProps = {
	ident: string
	data: FullmaktTypes
}

export const FullmaktVisning = ({ ident, data }: FullmaktProps) => {
	const { fullmektig } = useFullmektig(ident)
	if (!data?.fullmaktsgiver && !fullmektig) {
		return null
	}
	return (
		<>
			<TitleValue title="Fullmaktsgiver" value={data.fullmaktsgiver} />
			<TitleValue title="Gyldig fra" value={formatDate(data.gyldigFraOgMed)} />
			<TitleValue title="Gyldig til" value={formatDate(data.gyldigTilOgMed)} />
			{data.omraade.map((omraade, index) => (
				<TitleValue key={index} title={omraade.tema} value={omraade.handling.join(', ')} />
			))}
			<TitleValue title="Registrert" value={formatDate(data.registrert)} />
			<TitleValue title="Kilde" value={data.kilde} />
			<TitleValue title="Opphørt" value={oversettBoolean(data.opphoert)} />
		</>
	)
}
