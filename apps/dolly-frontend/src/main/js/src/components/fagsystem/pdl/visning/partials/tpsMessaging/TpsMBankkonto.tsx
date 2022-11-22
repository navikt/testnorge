import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { NorskBankkonto, UtenlandskBankkonto } from '~/components/fagsystem/bankkonto/visning'

type TpsMBankkontoProps = {
	data: any
	ident: string
	loading?: boolean
}

export const TpsMBankkonto = ({ data, ident, loading = false }: TpsMBankkontoProps) => {
	if (loading) {
		return <Loading />
	}
	if (!data) {
		return null
	}

	return (
		<>
			<UtenlandskBankkonto data={data.bankkontonrUtland} ident={ident} />
			<NorskBankkonto data={data.bankkontonrNorsk} ident={ident} />
		</>
	)
}
