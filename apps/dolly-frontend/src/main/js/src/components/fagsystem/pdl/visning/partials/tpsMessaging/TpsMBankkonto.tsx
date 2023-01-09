import Loading from '@/components/ui/loading/Loading'
import { NorskBankkonto, UtenlandskBankkonto } from '@/components/fagsystem/bankkonto/visning'

type TpsMBankkontoProps = {
	data: any
	loading?: boolean
	ident?: string
	extraButtons?: boolean
}

export const TpsMBankkonto = ({
	data,
	loading = false,
	ident = null,
	extraButtons = false,
}: TpsMBankkontoProps) => {
	if (loading) {
		return <Loading label="Laster bankkonto-data" />
	}
	if (!data) {
		return null
	}

	return (
		<>
			<UtenlandskBankkonto
				data={data.bankkontonrUtland}
				ident={ident}
				extraButtons={extraButtons}
			/>
			<NorskBankkonto data={data.bankkontonrNorsk} ident={ident} extraButtons={extraButtons} />
		</>
	)
}
