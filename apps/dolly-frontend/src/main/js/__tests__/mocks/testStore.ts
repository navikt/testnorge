import { configureStore, createSlice } from '@reduxjs/toolkit'

const finnPersonSlice = createSlice({
	name: 'finnPerson',
	initialState: {
		feilmelding: '',
		navigerTilGruppe: null,
	},
	reducers: {
		resetFeilmelding: (state) => {
			state.feilmelding = ''
		},
		navigerTilPerson: (state, action) => {
			// In tests, we just need to handle the action, not the actual navigation logic
			state.feilmelding = ''
		},
		navigerTilBestilling: (state, action) => {
			// Same for this action
			state.feilmelding = ''
		},
	},
})

export default function createTestStore() {
	return configureStore({
		reducer: {
			finnPerson: finnPersonSlice.reducer,
		},
		middleware: (getDefaultMiddleware) =>
			getDefaultMiddleware({
				serializableCheck: false,
			}),
	})
}
