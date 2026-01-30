import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { KodeverkTitleValue } from '@/components/fagsystem/skattekort/visning/Visning'

interface ForskuddstrekkDTO {
	trekkode?: string
	frikortBeloep?: number
	tabell?: string
	prosentSats?: number
	antallMndForTrekk?: number
}

interface ForskuddstrekkVisningProps {
	trekkliste?: ForskuddstrekkDTO[]
}

export const ForskuddstrekkVisning = ({ trekkliste }: ForskuddstrekkVisningProps) => {
	if (!trekkliste || trekkliste?.length < 1) {
		return null
	}

	return (
		<DollyFieldArray header="Forskuddstrekk" data={trekkliste} nested whiteBackground>
			{(trekk: ForskuddstrekkDTO, idx: number) => {
				return (
					<div className="person-visning_content" key={idx}>
						<KodeverkTitleValue kodeverkstype="TREKKODE" value={trekk?.trekkode} label="Trekkode" />
						<TitleValue title="Frikortbeløp" value={trekk?.frikortBeloep} />
						<TitleValue title="Tabell" value={trekk?.tabell} />
						<TitleValue title="Prosentsats" value={trekk?.prosentSats} />
						<TitleValue title="Antall måneder for trekk" value={trekk?.antallMndForTrekk} />
					</div>
				)
			}}
		</DollyFieldArray>
	)
}
