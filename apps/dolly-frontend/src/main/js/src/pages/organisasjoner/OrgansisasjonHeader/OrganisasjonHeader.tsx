import React, { Fragment } from 'react'
import { EksporterExcel } from '@/pages/gruppe/EksporterExcel/EksporterExcel'
import { Header } from '@/components/ui/header/Header'
import './OrganisasjonHeader.less'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import cn from 'classnames'
import Icon from '@/components/ui/icon/Icon'

type OrgHeaderProps = {
	antallOrganisasjoner: number | null
}

const OrganisasjonHeader = ({ antallOrganisasjoner }: OrgHeaderProps) => {
	const {
		currentBruker: { brukerId, brukernavn },
	} = useCurrentBruker()

	return (
		<Fragment>
			<header className={cn('content-header organisasjon-header')}>
				<div className="content-header_content">
					<div className="flexbox">
						<div className="content-header_icon organisasjon-header">
							<Icon kind="organisasjon" fontSize={'2.8rem'} />
						</div>
						<Header.TitleValue title="Eier" value={brukernavn} />
						<Header.TitleValue title="Antall hovedorganisasjoner" value={antallOrganisasjoner} />
					</div>
					<div className="gruppe-header__border" />
					<div className="gruppe-header__actions">
						<EksporterExcel brukerId={brukerId} />
					</div>
				</div>
			</header>
		</Fragment>
	)
}
export default OrganisasjonHeader
