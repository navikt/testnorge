import { Button, VStack } from '@navikt/ds-react'
import styled from 'styled-components'
import { ChatElipsisIcon, EnvelopeClosedIcon } from '@navikt/aksel-icons'
import '@/styles/variables.less'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

const Panel = styled.div`
	background-color: #0067c5;
	color: white;
	padding: 5px 20px 25px 20px;
	border-radius: 8px;
	width: 300px;

	p {
		margin-bottom: 25px;
		line-height: normal;
	}
`

const Header = styled.div`
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: -18px;

	h2 {
		font-size: 1.8em;
	}
`

export const KontaktinfoPanel = ({ setOpenState, openKontaktskjema }: any) => {
	const { currentBruker } = useCurrentBruker()
	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	const closePanel = () => setOpenState(false)

	return (
		<Panel>
			<Header>
				<h2>Kontakt oss</h2>
				<Button size="small" onClick={closePanel}>
					<Icon kind={'chevron-down'} />
				</Button>
			</Header>
			<p>
				{`Team Dolly er tilgjengelige på ${bankIdBruker ? '' : 'Slack (#dolly), '}e-post (dolly@nav.no), og via kontaktskjema.`}
			</p>
			<VStack gap="2">
				{!bankIdBruker && (
					<a href={'https://nav-it.slack.com/archives/CA3P9NGA2'} target="_blank">
						<Button
							variant="secondary"
							iconPosition="left"
							icon={<Icon kind="slack" />}
							style={{ width: '100%' }}
							onClick={closePanel}
						>
							Gå til Slack-kanal
						</Button>
					</a>
				)}
				<a href={'mailto:dolly@nav.no'}>
					<Button
						variant="secondary"
						iconPosition="left"
						icon={<EnvelopeClosedIcon />}
						style={{ width: '100%' }}
						onClick={closePanel}
					>
						Send e-post
					</Button>
				</a>
				<Button
					data-testid={TestComponentSelectors.BUTTON_OPEN_KONTAKTSKJEMA}
					variant="secondary"
					iconPosition="left"
					icon={<ChatElipsisIcon />}
					style={{ width: '100%' }}
					onClick={openKontaktskjema}
				>
					Åpne kontaktskjema
				</Button>
			</VStack>
		</Panel>
	)
}
