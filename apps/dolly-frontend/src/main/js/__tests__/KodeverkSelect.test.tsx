import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { dollyTest } from '../vitest.setup'
import { worker } from './mocks/browser'
import { userEvent } from '@vitest/browser/context'
import { act } from 'react'

dollyTest(
	'renders select with no options on empty list, and retries until options found',
	async () => {
		render(<DollySelect kodeverk={'test'} name={'test'} label={'Tester kodeverk'} />)

		expect(screen.getByText('Tester kodeverk')).toBeInTheDocument()

		expect(screen.getByText('Laster ...')).toBeInTheDocument()

		worker.use(
			// override the initial kodeverk request handler to return non-empty list
			http.get('/testnav-kodeverk-service/api/v1/kodeverk/test', () => {
				return HttpResponse.json({ koder: [{ value: 'kodeverk2', label: 'kodeverk2' }] })
			}),
		)

		await waitFor(() => expect(screen.getByText('Velg ...')).toBeInTheDocument(), { timeout: 2000 })

		const select = screen.getByRole('combobox')
		await act(async () => {
			await userEvent.click(select)
		})

		await waitFor(() => expect(screen.getByText('kodeverk2')).toBeInTheDocument(), {
			timeout: 2000,
		})
	},
)
