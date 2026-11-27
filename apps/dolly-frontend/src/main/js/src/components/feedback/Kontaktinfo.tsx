import * as React from 'react'
import { useRef, useState } from 'react'
// @ts-ignore
import './Kontaktinfo.less'
import { KontaktDialog } from '@/components/feedback/KontaktDialog'
import useBoolean from '@/utils/hooks/useBoolean'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Popover } from '@navikt/ds-react'
import { KontaktinfoPanel } from '@/components/feedback/KontaktinfoPanel'

export const Kontaktinfo = () => {
	const buttonRef = useRef<HTMLButtonElement>(null)
	const [openState, setOpenState] = useState(false)
	const [isKontaktskjemaOpen, openKontaktskjema, closeKontaktskjema] = useBoolean(false)

	return (
		<ErrorBoundary>
			<button
				ref={buttonRef}
				data-testid={TestComponentSelectors.BUTTON_OPEN_KONTAKTINFO}
				className={'btn-dialog'}
				onClick={() => setOpenState(!openState)}
				title="Kontakt oss"
			>
				<Icon kind={openState ? 'collapse' : 'krr'} fontSize={openState ? '2.5rem' : '2rem'} />
			</button>
			<Popover
				open={openState}
				onClose={() => setOpenState(false)}
				anchorEl={buttonRef.current}
				placement={'top-end'}
				style={{ border: 'none' }}
			>
				<KontaktinfoPanel setOpenState={setOpenState} openKontaktskjema={openKontaktskjema} />
			</Popover>
			{isKontaktskjemaOpen && <KontaktDialog closeDialog={closeKontaktskjema} />}
		</ErrorBoundary>
	)
}
