import React, { useEffect, useState } from 'react'
import * as _ from 'lodash-es'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import 'rc-tooltip/assets/bootstrap.css'
import { OrganisasjonItem } from '@/components/ui/icon/IconItem'
import Icon from '@/components/ui/icon/Icon'
import { OrganisasjonVisning } from '@/components/fagsystem/organisasjoner/visning/Visning'
import { EnhetBestilling, EnhetData } from '@/components/fagsystem/organisasjoner/types'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { useDollyOrganisasjoner } from '@/utils/hooks/useDollyOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'
import { Organisasjon } from '@/service/services/organisasjonforvalter/types'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useReduxSelector } from '@/utils/hooks/useRedux'
import { organisasjonerSideStoerrelseLocalStorageKey } from '@/utils/constants/localStorage'

type OrganisasjonListeProps = {
	bestillinger: Array<EnhetBestilling>
	setAntallOrg: Function
	sidetall: number
}

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function OrganisasjonListe({
	bestillinger,
	setAntallOrg,
	sidetall,
}: OrganisasjonListeProps) {
	const { currentBruker } = useCurrentBruker()
	const search = useReduxSelector((state) => state.search)

	const sokSelectorOrg = (
		items: {
			organisasjonsnavn: string
			enhetstype: string
			bestillingId: number[]
			orgInfo: Organisasjon
			id: number
			organisasjonsnummer: string
			status: string
		}[],
		searchStr: string,
	) => {
		if (!items) {
			return []
		}
		if (!searchStr) {
			return items
		}

		const query = searchStr.toLowerCase()
		return items.filter((item) =>
			Object.values(item).some((v) => (v || '').toString().toLowerCase().includes(query)),
		)
	}

	const hentOrgStatus = (
		bestillingArray: Array<EnhetBestilling>,
		bestillingId: string | number,
	) => {
		if (!bestillingArray) {
			return null
		}
		let orgStatus = 'Ferdig'
		const bestilling = bestillingArray.find((obj) => {
			return obj.id === bestillingId
		})
		if (!bestilling?.status || bestilling.feil) {
			orgStatus = 'Feilet'
		}
		bestilling?.status?.[0].statuser?.forEach((status) => {
			if (status?.melding !== 'OK') {
				orgStatus = 'Avvik'
			}
		})
		return orgStatus
	}

	const getBestillingIdFromOrgnummer = (
		bestillingListe: Array<EnhetBestilling>,
		organisasjonsnummer: string,
	) =>
		bestillingListe
			.filter((org) => org?.organisasjonNummer === organisasjonsnummer)
			.map((org) => org.id)
			.sort(function (a: number, b: number) {
				return b - a
			})

	const mergeList = (orgListe: Array<Organisasjon>, bestillingListe: Array<EnhetBestilling>) => {
		if (_.isEmpty(orgListe)) {
			return null
		}

		return orgListe.map((orgInfo) => {
			const bestillingId = getBestillingIdFromOrgnummer(
				bestillingListe,
				orgInfo.organisasjonsnummer,
			)
			return {
				orgInfo,
				id: orgInfo.id,
				organisasjonsnummer: orgInfo.organisasjonsnummer,
				organisasjonsnavn: orgInfo.organisasjonsnavn,
				enhetstype: orgInfo.enhetstype,
				status: hentOrgStatus(bestillingListe, bestillingId[0]),
				bestillingId: bestillingId,
			}
		})
	}

	const { organisasjoner, loading } = useDollyOrganisasjoner(
		currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId,
	)

	const [filtrertOrgListe, setfiltrertOrgListe] = useState([])

	useEffect(() => {
		setAntallOrg(organisasjoner?.length)
		const sortedOrgliste = _.orderBy(organisasjoner, ['id'], ['desc'])
		setfiltrertOrgListe(sokSelectorOrg(mergeList(sortedOrgliste, bestillinger), search))
	}, [organisasjoner, bestillinger, search])

	if (loading) {
		return <Loading label="Laster organisasjoner" panel />
	}
	const columns = [
		{
			text: 'Orgnr.',
			width: '20',
			dataField: 'organisasjonsnummer',
			unique: true,

			formatter: (_cell: string, row: EnhetData) => (
				<DollyCopyButton
					displayText={row.organisasjonsnummer}
					copyText={row.organisasjonsnummer}
					tooltipText={'Kopier organisasjonsnummer'}
				/>
			),
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'organisasjonsnavn',
		},
		{
			text: 'Enhetstype',
			width: '15',
			dataField: 'enhetstype',
		},
		{
			text: 'Bestilling-ID',
			width: '20',
			dataField: 'bestillingId',
			formatter: (_cell: number, row: EnhetData) => {
				const str = row.bestillingId
				if (str.length > 1) {
					return `${str[0]} ...`
				}
				return str[0]
			},
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'status',
			formatter(cell: string) {
				// @ts-ignore
				return <Icon kind={ikonTypeMap[cell]} title={cell} />
			},
		},
	]

	return (
		<ErrorBoundary>
			<DollyTable
				data={filtrertOrgListe}
				columns={columns}
				visSide={sidetall}
				pagination
				manualLocalStorageKey={organisasjonerSideStoerrelseLocalStorageKey}
				iconItem={<OrganisasjonItem />}
				onExpand={(organisasjon: EnhetData) => (
					<OrganisasjonVisning data={organisasjon} bestillinger={bestillinger} />
				)}
			/>
		</ErrorBoundary>
	)
}
