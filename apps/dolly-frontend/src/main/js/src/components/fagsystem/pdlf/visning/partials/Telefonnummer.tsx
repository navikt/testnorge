import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { TelefonData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type DataListe = {
	data: Array<TelefonData>
}

type Data = {
	item: TelefonData
	idx: number
}

export const Telefonnummer = ({ data }: DataListe) => {
	if (!data || data.length === 0) return null

	const TelefonnummerVisning = ({ item, idx }: Data) => {
		const telefonnummer = item.nummer || item.telefonnummer
		const landkode = item.landskode || item.landkode
		const prioritet =
			item.prioritet || (item.telefontype === 'MOBI' && 1) || (item.telefontype === 'HJET' && 2)

		return (
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Telefonnummer" value={`${landkode} ${telefonnummer}`} />
				<TitleValue title="Prioritet" value={prioritet} />
			</div>
		)
	}

	return (
		<div>
			<SubOverskrift label="Telefonnummer" iconKind="telephone" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: TelefonData, idx: number) => <TelefonnummerVisning item={item} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
