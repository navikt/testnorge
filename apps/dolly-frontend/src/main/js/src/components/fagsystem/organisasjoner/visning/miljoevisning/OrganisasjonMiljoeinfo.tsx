import { useAsync } from 'react-use'
import { OrgforvalterApi } from '@/service/Api'
import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { OrganisasjonDataVisning } from '@/components/fagsystem/organisasjoner/visning/miljoevisning/OrganisasjonDataVisning'

export const OrganisasjonMiljoeinfo = (props: { orgnummer: string }) => {
	const state = useAsync(async () => {
		if (props) {
			return OrgforvalterApi.getOrganisasjonerMiljoeInfo(props.orgnummer)
		}
	}, [])

	if (!props) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Opprettet i miljøer" iconKind="vis-org-data" />
			{state.loading && <Loading label="Laster miljøer" />}
			{/* @ts-ignore */}
			{state.value && <OrganisasjonDataVisning data={state.value.data} />}
			{state.value && (
				<p>
					<i>
						Hold pekeren over et miljø for å se dataene som finnes på denne organisasjonen i EREG
						for det aktuelle miljøet.
					</i>
				</p>
			)}
		</div>
	)
}
