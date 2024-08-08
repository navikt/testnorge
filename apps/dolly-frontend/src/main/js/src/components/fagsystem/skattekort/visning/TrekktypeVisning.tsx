import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showKodeverkLabel } from '@/components/fagsystem/skattekort/visning/Visning'

export const TrekktypeVisning = ({ trekkliste }) => {
	return (
		<DollyFieldArray header="Forskuddstrekk" data={trekkliste} nested whiteBackground>
			{(trekk, idx) => {
				const forskuddstrekk = trekk?.forskuddstrekk

				return (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Trekktype" value={forskuddstrekk?.trekktype} />
						<TitleValue
							title="Trekkode"
							value={showKodeverkLabel('TREKKODE', forskuddstrekk?.trekkode)}
						/>
						<TitleValue title="Frikortbeløp" value={forskuddstrekk?.frikortbeloep} />
						<TitleValue
							title="Tabelltype"
							value={showKodeverkLabel('TABELLTYPE', forskuddstrekk?.tabelltype)}
						/>
						<TitleValue title="Tabellnummer" value={forskuddstrekk?.tabellnummer} />
						<TitleValue title="Prosentsats" value={forskuddstrekk?.prosentsats} />
						<TitleValue
							title="Antall måneder for trekk"
							value={forskuddstrekk?.antallMaanederForTrekk}
						/>
					</div>
				)
			}}
		</DollyFieldArray>
	)
}
