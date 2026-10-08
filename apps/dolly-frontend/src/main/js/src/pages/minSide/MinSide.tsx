import Maler from './maler/Maloversikt'
import Profil from './Profil'

import './MinSide.less'
import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Box, Tabs } from '@navikt/ds-react'
import { BarChartIcon, BookmarkIcon } from '@navikt/aksel-icons'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { BrukerStatistikk } from './statistikk/BrukerStatistikk'

enum MinSideVisning {
	MALER = 'maler',
	STATISTIKK = 'statistikk',
}

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()
	const statistikkEierId = currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId

	return (
		<>
			<h1>Min side</h1>
			<ErrorBoundary>
				<Profil />
				{brukerProfil && (
					<Tabs defaultValue={MinSideVisning.MALER}>
						<Box width="fit-content" maxWidth="100%">
							<Tabs.List>
								<Tabs.Tab
									data-testid={TestComponentSelectors.TOGGLE_MIN_SIDE_MALER}
									value={MinSideVisning.MALER}
									label="Mine maler"
									icon={<BookmarkIcon aria-hidden />}
								/>
								<Tabs.Tab
									data-testid={TestComponentSelectors.TOGGLE_MIN_SIDE_STATISTIKK}
									value={MinSideVisning.STATISTIKK}
									label="Statistikk"
									icon={<BarChartIcon aria-hidden />}
								/>
							</Tabs.List>
						</Box>
						<Tabs.Panel value={MinSideVisning.MALER}>
							<Box paddingBlock="space-24 space-0">
								<Maler brukerId={currentBruker?.brukerId} />
							</Box>
						</Tabs.Panel>
						<Tabs.Panel value={MinSideVisning.STATISTIKK}>
							<ErrorBoundary>
								<Box paddingBlock="space-24 space-0">
									<BrukerStatistikk
										key={statistikkEierId}
										eierId={statistikkEierId}
										teamNavn={currentBruker?.representererTeam?.navn}
									/>
								</Box>
							</ErrorBoundary>
						</Tabs.Panel>
					</Tabs>
				)}
			</ErrorBoundary>
		</>
	)
}
