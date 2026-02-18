import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel } from '@/utils/DataFormatter'

interface ForskuddstrekkDTO {
	trekkode: string
	frikort?: FrikortDTO
	prosentkort?: ProsentkortDTO
	trekktabell?: TabellkortDTO
}

interface FrikortDTO {
	frikortBeloep: number
}

interface ProsentkortDTO {
	prosentSats: number
	antallMndForTrekk?: number
}

interface TabellkortDTO {
	tabell: string
	prosentSats: number
	antallMndForTrekk: number
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
						<TitleValue title="Trekkode" value={codeToNorskLabel(trekk?.trekkode)} />
						<TitleValue
							title="Frikortbeløp"
							value={
								trekk?.frikort !== undefined
									? trekk?.frikort?.frikortBeloep === undefined
										? 'Ingen beløpsgrense'
										: trekk?.frikort?.frikortBeloep
									: undefined
							}
						/>
						<TitleValue title="Prosentsats" value={trekk?.prosentkort?.prosentSats} />
						<TitleValue
							title="Antall måneder for trekk"
							value={trekk?.prosentkort?.antallMndForTrekk}
						/>
						<TitleValue title="Tabell" value={trekk?.trekktabell?.tabell} />
						<TitleValue title="Prosentsats" value={trekk?.trekktabell?.prosentSats} />
						<TitleValue
							title="Antall måneder for trekk"
							value={trekk?.trekktabell?.antallMndForTrekk}
						/>
					</div>
				)
			}}
		</DollyFieldArray>
	)
}
