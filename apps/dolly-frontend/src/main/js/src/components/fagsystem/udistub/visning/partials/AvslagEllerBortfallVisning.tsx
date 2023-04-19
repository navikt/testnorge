import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatStringDates } from '@/utils/DataFormatter'

export type AvslagEllerBortfall = {
	avslagEllerBortfall: {
		avgjorelsesDato: Date
	}
}

export const AvslagEllerBortfallVisning = ({ avslagEllerBortfall }: AvslagEllerBortfall) =>
	avslagEllerBortfall && Object.values(avslagEllerBortfall).some((item) => item !== null) ? (
		<>
			<h4>Avslag eller bortfall</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Avgjørelsesdato"
					value={formatStringDates(avslagEllerBortfall.avgjorelsesDato)}
				/>
			</div>
		</>
	) : null
