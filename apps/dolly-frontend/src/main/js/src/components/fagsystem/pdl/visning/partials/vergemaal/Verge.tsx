import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { VergeEllerFullmektig } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type VergeProps = {
	data: VergeEllerFullmektig
	harFullmektig: boolean
}

export const Verge = ({ data, harFullmektig }: VergeProps) => {
	if (!data) {
		return null
	}

	return (
		<>
			<div className="person-visning_content">
				<h4 style={{ width: '100%' }}>{harFullmektig ? 'Fullmektig' : 'Verge'}</h4>
				<TitleValue title="Ident" value={data.motpartsPersonident} visKopier />
				<TitleValue title="Fornavn" value={data.navn?.fornavn} />
				<TitleValue title="Mellomnavn" value={data.navn?.mellomnavn} />
				<TitleValue title="Etternavn" value={data.navn?.etternavn} />
			</div>
		</>
	)
}
