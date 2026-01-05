import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { KodeverkTitleValue } from '@/components/fagsystem/skattekort/visning/Visning'
import { toTitleCase } from '@/utils/DataFormatter'

export const ForskuddstrekkVisning = ({ trekkliste }: any) => {
	if (!trekkliste || trekkliste?.length < 1) {
		return null
	}

	return (
		<DollyFieldArray header="Forskuddstrekk" data={trekkliste} nested whiteBackground>
			{(trekk: any, idx: number) => {
				const forskuddstrekkType = Object.keys(trekk)?.filter((key) => trekk[key])?.[0]
				const forskuddstrekk = trekk[forskuddstrekkType]

				return (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Trekktype" value={toTitleCase(forskuddstrekkType)} />
						<KodeverkTitleValue
							kodeverkstype="TREKKODE"
							value={forskuddstrekk?.trekkode}
							label="Trekkode"
						/>
						<TitleValue title="Frikortbeløp" value={forskuddstrekk?.frikortbeloep} />
						<KodeverkTitleValue
							kodeverkstype="TABELLTYPE"
							value={forskuddstrekk?.tabelltype}
							label="Tabelltype"
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
