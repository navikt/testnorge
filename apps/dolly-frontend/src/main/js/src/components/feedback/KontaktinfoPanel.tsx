import { Button, VStack } from '@navikt/ds-react'
import styled from 'styled-components'
import { ChatElipsisIcon, ChevronDownIcon, EnvelopeClosedIcon } from '@navikt/aksel-icons'
import '@/styles/variables.less'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

const Panel = styled.div`
	background-color: var(--ax-bg-accent-strong);
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

const StyledButton = styled(Button)`
	background-color: var(--ax-bg-default);
	color: var(--ax-bg-accent-strong);
	width: 100%;

	&:hover {
		background-color: var(--ax-bg-accent-moderate-hover);
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
				<Button size="small" onClick={closePanel} icon={<ChevronDownIcon title="Lukk" />} />
			</Header>
			<p>
				{`Team Dolly er tilgjengelige på ${bankIdBruker ? '' : 'Slack (#dolly), '}e-post (dolly@nav.no), og via kontaktskjema.`}
			</p>
			<VStack gap="space-12">
				{!bankIdBruker && (
					<a href={'https://nav-it.slack.com/archives/CA3P9NGA2'} target="_blank">
						<StyledButton
							iconPosition="left"
							icon={<Icon kind="slack" fill="#2176d4" />}
							onClick={closePanel}
						>
							Gå til Slack-kanal
						</StyledButton>
					</a>
				)}
				<a href={'mailto:dolly@nav.no'}>
					<StyledButton
						iconPosition="left"
						icon={<EnvelopeClosedIcon aria-hidden />}
						style={{ width: '100%' }}
						onClick={closePanel}
					>
						Send e-post
					</StyledButton>
				</a>
				<StyledButton
					data-testid={TestComponentSelectors.BUTTON_OPEN_KONTAKTSKJEMA}
					iconPosition="left"
					icon={<ChatElipsisIcon aria-hidden />}
					style={{ width: '100%' }}
					onClick={openKontaktskjema}
				>
					Åpne kontaktskjema
				</StyledButton>
			</VStack>
		</Panel>
	)
}
