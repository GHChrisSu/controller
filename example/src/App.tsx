import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import { diffuse } from 'aromajoin_controller';

export default function App() {
  React.useEffect(() => {
    diffuse([0]);
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result:</Text>
      <Button title="Diffuse" onPress={() => diffuse([0])} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
