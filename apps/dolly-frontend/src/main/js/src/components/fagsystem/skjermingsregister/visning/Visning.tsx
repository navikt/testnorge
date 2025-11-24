import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { addDays, isBefore } from 'date-fns'
import { formatDate } from '@/utils/DataFormatter'
import { Skjerming } from '@/components/fagsystem/skjermingsregister/SkjermingTypes'

type SkjermingProps = {
	data: Skjerming
}

export const SkjermingVisning = ({ data }: SkjermingProps) => {
	if (!data?.skjermetFra) {
		return null
	}
	return (
		<>
			<TitleValue
				title="Har skjerming"
				value={
					data.skjermetTil && isBefore(new Date(data.skjermetTil), addDays(new Date(), -1))
						? 'Nei'
						: 'Ja'
				}
			/>
			<TitleValue title="Skjerming fra" value={formatDate(data.skjermetFra)} />
			{data?.skjermetTil && (
				<TitleValue title="Skjerming til" value={formatDate(data.skjermetTil)} />
			)}
		</>
	)
}
