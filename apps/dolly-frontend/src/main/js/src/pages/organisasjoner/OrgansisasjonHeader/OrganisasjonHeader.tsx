import React, { Fragment } from 'react'
import { EksporterExcel } from '@/pages/gruppe/EksporterExcel/EksporterExcel'
import { Header } from '@/components/ui/header/Header'
import './OrganisasjonHeader.less'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

type OrgHeaderProps = {
	antallOrganisasjoner: number
	getOrgExcelFil: Function
	isFetchingExcel: boolean
}

const OrganisasjonHeader = ({
	antallOrganisasjoner,
	getOrgExcelFil,
	isFetchingExcel,
}: OrgHeaderProps) => {
	const {
		currentBruker: { brukerId, brukernavn },
	} = useCurrentBruker()

	return (
		<Fragment>
			<Header className={'organisasjon-header'} icon={'organisasjon'}>
				<div className="flexbox">
					<Header.TitleValue title="Eier" value={brukernavn} />
					<Header.TitleValue title="Antall hovedorganisasjoner" value={antallOrganisasjoner} />
				</div>
				<div className="gruppe-header__actions">
					<EksporterExcel
						exportId={brukerId}
						filPrefix={'org'}
						action={getOrgExcelFil}
						loading={isFetchingExcel}
					/>
				</div>
			</Header>
		</Fragment>
	)
}
export default OrganisasjonHeader
