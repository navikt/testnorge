import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { SkjermingVisning } from '@/components/fagsystem/skjermingsregister/visning/Visning'

export const NavAnsattVisning = ({ nomData, nomLoading, skjermingData }) => {
	if (nomLoading) return <Loading label="Laster NOM-data" />

	if (!nomData && !skjermingData) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Nav-ansatt" iconKind="nav" />
			<ErrorBoundary>
				{nomData && (
					<>
						<h4 style={{ marginTop: '0px' }}>Nav-ansatt (NOM)</h4>
						<div className="person-visning_content">
							<TitleValue title="Nav-ident" value={nomData.navident} />
							<TitleValue title="Startdato" value={formatDate(nomData.startDato)} />
							<TitleValue title="Sluttdato" value={formatDate(nomData.sluttDato)} />
						</div>
					</>
				)}
				{skjermingData && (
					<>
						<h4 style={{ marginTop: nomData ? '15px' : '0px' }}>Skjerming (egen ansatt)</h4>
						<div className="person-visning_content">
							<SkjermingVisning data={skjermingData} />
						</div>
					</>
				)}
			</ErrorBoundary>
		</>
	)
}
