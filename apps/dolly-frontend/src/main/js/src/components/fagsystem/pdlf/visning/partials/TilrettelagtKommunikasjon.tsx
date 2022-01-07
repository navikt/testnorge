import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { TilrettelagtKommunikasjonData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type DataListe = {
	data: Array<TilrettelagtKommunikasjonData>
}

type Data = {
	item: TilrettelagtKommunikasjonData
	idx: number
}

export const TilrettelagtKommunikasjon = ({ data }: DataListe) => {
	if (!data || data.length === 0) return null

	const TilrettelagtKommunikasjonVisning = ({ item, idx }: Data) => {
		return (
			<div className="person-visning_content" key={idx}>
				<TitleValue
					title="Talespråk"
					value={item.talespraaktolk ? item.talespraaktolk.spraak : item.spraakForTaletolk}
					kodeverk={PersoninformasjonKodeverk.Spraak}
				/>
				<TitleValue
					title="Tegnspråk"
					value={item.tegnspraaktolk ? item.tegnspraaktolk.spraak : item.spraakForTegnspraakTolk}
					kodeverk={PersoninformasjonKodeverk.Spraak}
				/>
			</div>
		)
	}

	return (
		<div>
			<SubOverskrift label="Tilrettelagt kommunikasjon" iconKind="kommentar" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="Tolk">
						{(item: TilrettelagtKommunikasjonData, idx: number) => (
							<TilrettelagtKommunikasjonVisning item={item} idx={idx} />
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
